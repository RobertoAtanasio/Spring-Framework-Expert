package com.algaworks.brewer.repository.helper.estilo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;

public class EstilosImpl implements EstilosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
//		System.out.println(">>>>>>>>> paginaAtual: " + paginaAtual);
//		System.out.println(">>>>>>>>> totalRegistrosPorPagina: " + totalRegistrosPorPagina);
//		System.out.println(">>>>>>>>> primeiroRegistro: " + primeiroRegistro);
		
		CriteriaBuilder cb = manager.getCriteriaBuilder();
	    CriteriaQuery<Estilo> cq = cb.createQuery(Estilo.class);
	    Root<Estilo> root = cq.from(Estilo.class);
	    
	    Sort sort = pageable.getSort();
	    
	    if (sort != null) {
	    	
	    	Order order = sort.iterator().next();
			String property = order.getProperty();
			
			if (order.isAscending()) {
				cq.orderBy(cb.asc(root.get(property)));
			} else {
				cq.orderBy(cb.desc(root.get(property)));	
			}
	    }
	    
	    if (!StringUtils.isEmpty(filtro.getNome())) {
	    	cq.select(root).where(
	    			cb.like(root.get("nome"), "%" + filtro.getNome() + "%")
		    );
	    } else {
	    	cq.select(root);
	    }
	    
	    List<Estilo> lista = manager.createQuery(cq)
	    		.setFirstResult(primeiroRegistro)
	    		.setMaxResults(totalRegistrosPorPagina)
	    		.getResultList();
	    
	    int qtde = manager.createQuery(cq).getResultList().size();
	    
	    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
	}
	
}
