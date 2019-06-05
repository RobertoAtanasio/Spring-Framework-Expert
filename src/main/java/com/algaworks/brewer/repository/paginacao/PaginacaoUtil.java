package com.algaworks.brewer.repository.paginacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginacaoUtil {

	public void preparar(Criteria criteria, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
		criteria.setFirstResult(primeiroRegistro);
		criteria.setMaxResults(totalRegistrosPorPagina);
		
		Sort sort = pageable.getSort();
		if (sort != null) {
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property));
		}
	}
	
//	public void prepararSort(Pageable pageable, CriteriaBuilder cb, CriteriaQuery<Cerveja> cq, Root<Cerveja> root) {
//		
//		Sort sort = pageable.getSort();
//
//		if (sort != null) {
//
//			Order order = sort.iterator().next();
//
//			String property = order.getProperty();
//
//			if (order.isAscending()) {
//				cq.orderBy(cb.asc(root.get(property)));
//			} else {
//				cq.orderBy(cb.desc(root.get(property)));
//			}
//
//		}
//		
//	}
	
}
