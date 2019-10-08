package com.algaworks.brewer.repository.helper.cerveja;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.dto.ValorItensEstoque;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.storage.FotoStorage;

// obs: o nome da classe deve ser incicialmente igual à classe do repositório Cervejas.java. O que se pode mudar é o sufixo Impl
// ver a explicação do sufixo na observação da classe JPAConfig.java
public class CervejasImpl implements CervejasQueries {
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<Cerveja> cq = cb.createQuery(Cerveja.class);
	    Root<Cerveja> root = cq.from(Cerveja.class);
	    	    
	    org.springframework.data.domain.Sort sort = pageable.getSort();

		if (sort != null && sort.isSorted()) {

			org.springframework.data.domain.Sort.Order order = sort.iterator().next();

			String property = order.getProperty();

			if (order.isAscending()) {
				cq.orderBy(cb.asc(root.get(property)));
			} else {
				cq.orderBy(cb.desc(root.get(property)));
			}

		}
	    	    
		cq.select(root);
		
	    if (!StringUtils.isEmpty(filtro.getSku())) {
	    	cq.select(root).where(cb.equal(root.get("sku"), filtro.getSku()));
	    } 
	    
	    if (!StringUtils.isEmpty(filtro.getNome())) {
	    	cq.where(cb.like(root.get("nome"), "%" + filtro.getNome() + "%"));
	    } 
	    
	    int qtde = em.createQuery(cq).getResultList().size();
	    
	    List<Cerveja> lista = em.createQuery(cq)
	    		.setFirstResult(primeiroRegistro)
	    		.setMaxResults(totalRegistrosPorPagina)
	    		.getResultList();
	    
	    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
	}

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "select new com.algaworks.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";
		List<CervejaDTO> cervejasFiltradas = em.createQuery(jpql, CervejaDTO.class)
					.setParameter("skuOuNome", skuOuNome + "%")
					.getResultList();
		
//		for (CervejaDTO x : cervejasFiltradas) {
//			System.out.println(x.getSku() + " - " + x.getNome() + " - " + x.getOrigem());
//		}

		cervejasFiltradas.forEach(c -> c.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));
		return cervejasFiltradas;
	}

	@Override
	public ValorItensEstoque valorItensEstoque() {
		String jpql = "select new com.algaworks.brewer.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Cerveja";
		return em.createQuery(jpql, ValorItensEstoque.class).getSingleResult();
	}
}
