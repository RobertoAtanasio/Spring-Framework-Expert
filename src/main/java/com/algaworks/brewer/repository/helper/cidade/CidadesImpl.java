package com.algaworks.brewer.repository.helper.cidade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		
		// Esse método é chamado no CidadesController.java
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cidade> cq = cb.createQuery(Cidade.class);
	    Root<Cidade> root = cq.from(Cidade.class);
	    cq.orderBy(cb.asc(root.get("nome")));
		
	    Sort sort = pageable.getSort();
	    
	    if (sort != null && sort.isSorted()) {
	    	
	    	Order order = sort.iterator().next();
			String property = order.getProperty();
			
			if (order.isAscending()) {
				cq.orderBy(cb.asc(root.get(property)));
			} else {
				cq.orderBy(cb.desc(root.get(property)));	
			}
	    }
		
	    cq.select(root); 
	    
	    if (filtro != null) {
			if (filtro.getEstado() != null) {
				cq.select(root).where(cb.equal(root.get("estado"), filtro.getEstado()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				cq.select(root).where(cb.equal(root.get("nome"), filtro.getNome()));
			}
		}
	    
	    TypedQuery<Cidade> q = em.createQuery(cq)
	    		.setFirstResult(primeiroRegistro)
	    		.setMaxResults(totalRegistrosPorPagina);
	    List<Cidade> lista = q.getResultList();
	    
//	    List<Cliente> lista = em.createQuery(cq)
//	    		.setFirstResult(primeiroRegistro)
//	    		.setMaxResults(totalRegistrosPorPagina)
//	    		.getResultList();
	    
	    int qtde = em.createQuery(cq).getResultList().size();
	    
	    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
	}
	
	//================================================================================================
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar2(CidadeFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("estado", "e");
				
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	private Long total(CidadeFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(CidadeFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (filtro.getEstado() != null) {
				criteria.add(Restrictions.eq("estado", filtro.getEstado()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
		}
	}

}
