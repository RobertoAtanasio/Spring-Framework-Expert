package com.algaworks.brewer.repository.helper.usuario;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.UsuarioFilter;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager em;
	
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
				.setParameter("email", email)
				.getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		//--- internamente o acesso é feito pelo ID do usuário e não pelo classe
		return em
				.createQuery("select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
	
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
	    
	    int qtde;
	    List<Usuario> lista;
	    
	    String jpql = "select u from Usuario u";
	    
	    if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
	    	
	    	boolean inicio = true;
	    	
	    	jpql += " where u.codigo in (select ug.id.usuario from UsuarioGrupo ug where ug.id.grupo in (";
	    	
	    	for (Grupo f : filtro.getGrupos()) {
	    		if (inicio) {
	    			inicio = false;
	    			jpql += f.getCodigo().toString();
	    		} else {
	    			jpql += ", " + f.getCodigo().toString();	    			
	    		}
	    	}
	    	jpql += "))";
	    	
	    	if (!StringUtils.isEmpty(filtro.getNome())) {
	    		jpql += " AND nome like %" + filtro.getNome() + "%" ; 
	    	}
	    	
	    	if (!StringUtils.isEmpty(filtro.getEmail())) {	    		
	    		jpql += " AND email like %" + filtro.getEmail() + "%" ; 
	    	}
	    	
	    } 
	    
	    Sort sort = pageable.getSort();
	    
	    if (sort != null && sort.isSorted()) {
	    	
	    	Order order = sort.iterator().next();
			String property = order.getProperty();
			
			if (order.isAscending()) {
				jpql += " ORDER BY " + property + " ASC";
			} else {
				jpql += " ORDER BY " + property + " DESC";	
			}
	    }
		
		System.out.println("--->>> jpql: " + jpql); 
		
		qtde = em.createQuery(jpql, Usuario.class).getResultList().size();
		
		lista = em.createQuery(jpql, Usuario.class)
				.setFirstResult(primeiroRegistro)
				.setMaxResults(totalRegistrosPorPagina)
				.getResultList();
		
		for (Usuario c : lista) {
			System.out.println(c.getNome() + " - " + c.getEmail()); 
			for (Grupo g : c.getGrupos()) {
				System.out.println("-> " + g.getCodigo());
			}
		}
	    	    
	    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
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
	
}
