package com.algaworks.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		//--- acesso via JPQL
		//--- como não vai se fazer join com nenhuma outra tabela, o select u é opcional
		//--- lembrar que é JPQL e o acesso é tomado como referência uma classe e não select diretamente sobre o banco de
		//	  dados, logo a condição 'ativo = true', pois o campo é um boolean
//		return manager
//				.createQuery("select u from Usuario u where lower(u.email) = lower(:email) and u.ativo = true", Usuario.class)
//				.setParameter("email", email).getResultList().stream().findFirst();
		return em
				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		//--- internamente o acesso é feito pelo ID do usuário e não pelo classe
		return em
				.createQuery("select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		Criteria criteria = em.unwrap(Session.class).createCriteria(Usuario.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		List<Usuario> filtrados = criteria.list();
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos())); // executar o join em grupo
		
		return new PageImpl<>(filtrados, pageable, total(filtro));
	}
	
	@Transactional(readOnly = true)
	@Override
	public Usuario buscarComGrupos(Long codigo) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
		Root<Usuario> usuario = cq.from(Usuario.class);
		
		Join<Usuario, Grupo> grupo = (Join) usuario.fetch("grupos");
		
		cq.select(usuario);
		cq.where(cb.equal(usuario.get("codigo"), codigo));
		
		TypedQuery<Usuario> query = em.createQuery(cq);
		
		Usuario usuariolido = query.getSingleResult();

		em.close();
		
		return usuariolido;
	}
	
	private Long total(UsuarioFilter filtro) {
		Criteria criteria = em.unwrap(Session.class).createCriteria(Usuario.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(UsuarioFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}
			
			//--- o comando abaixo foi retirado por causa da inclusão da paginação. Como o limte
			//    incluído foi 3, a quantidade de registros lidos com o join de grupo não coincide 
			//    com a quantidade 3, logo, retira-se o join para ficar acessando apemas a tabela 
			//    de usuários e inclui-se o filtrados.forEach(u -> Hibernate.initialize(u.getGrupos()));
			//    no método filtrar()
//			criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);	// executa o JOIN dos grupos. Carrega os grupos
//																			// porque a lista de grupos de Usuario.java está
//																			// setado com @ManyToMany e por default o grupo
//																			// não é carregado. O JoinType.LEFT_OUTER_JOIN
//																			// é quem inicializa o código.
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				List<Criterion> subqueries = new ArrayList<>();
//				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(grupo -> grupo.getCodigo()).toArray()) {					
					
//					System.out.println(">>>>>>>> codigoGrupo: " + codigoGrupo);
					
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
					dc.setProjection(Projections.property("id.usuario"));
					
					subqueries.add(Subqueries.propertyIn("codigo", dc));
				}
				
				Criterion[] criterions = new Criterion[subqueries.size()];
				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
			}
		}
	}
	
}
