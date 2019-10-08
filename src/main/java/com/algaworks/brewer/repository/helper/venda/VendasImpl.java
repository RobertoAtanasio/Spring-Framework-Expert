package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMesDTO;
import com.algaworks.brewer.dto.VendaOrigemDTO;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> criteria = builder.createQuery(Venda.class);		
		Root<Venda> root = criteria.from(Venda.class);
		Join<Venda, Cliente> cliente = (Join) root.fetch("cliente");
		
		Predicate[] predicates = this.formatarFiltroPesquisa(filtro, builder, root, cliente);
		criteria.select(root);
		criteria.where(predicates);
		TypedQuery<Venda> query = manager.createQuery(criteria);

		adicionarRestricoesDePaginacao(pageable, query);
		
		Page<Venda> pagina = new PageImpl<>(query.getResultList(), pageable, this.qtdeTotalRegistros(filtro));
		
		return pagina;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Venda buscarComItens(Long codigo) {
		
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
		Root<Venda> venda = cq.from(Venda.class);
		
		Join<Venda, ItemVenda> item = (Join) venda.fetch("itens");
		
		cq.select(venda);
		cq.where(cb.equal(venda.get("codigo"), codigo));
		
		TypedQuery<Venda> query = manager.createQuery(cq);
		
		Venda vendaLida = query.getSingleResult();

		manager.close();
		
		return vendaLida;
	}
	
	@Override
	public BigDecimal valorTotalNoAno() {
		// o select está em JPQL onde os nomes dos atributos são os definidos na classe e não na tabela do BD!
		// A opção Optional foi usada caso não encontre nada retornaremos zero.
		// Obs.: no html, na tag <div class="aw-box__value">R$[[${{vendasNoAno}}]]</div>, a segundo {} comolca na formatação BR
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status"
						, BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery(
						  "select sum(valorTotal) from Venda where month(dataCriacao) = :mes" 
						+ " and year(dataCriacao) = :ano" 
						+ " and status = :status", BigDecimal.class)
					.setParameter("mes", MonthDay.now().getMonthValue())
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery(
					"select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMesDTO> totalPorMes() {
		
		//--- executar a consulta nativa. Ver main/resources/META-INF/orm.xml
		List<VendaMesDTO> vendasMes = manager.createNamedQuery("Vendas.totalPorMes", VendaMesDTO.class).getResultList();
		
		//--- o tratamento abaixo é para os casos onde não tem valor no ano/mês
		LocalDate hoje = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			
			// formata o mês atual. Por exemplo: 2019/06
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			
			boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasMes.add(i - 1, new VendaMesDTO(mesIdeal, 0));
			}
			
			hoje = hoje.minusMonths(1);		// obter a data do mês anterior
		}
		
		return vendasMes;
	}
	
	@Override
	public List<VendaOrigemDTO> totalPorOrigem() {
		
		List<VendaOrigemDTO> vendasNacionalidade = manager.createNamedQuery("Vendas.porOrigem", VendaOrigemDTO.class).getResultList();
		
		LocalDate now = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", now.getYear(), now.getMonth().getValue());
			
			boolean possuiMes = vendasNacionalidade.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasNacionalidade.add(i - 1, new VendaOrigemDTO(mesIdeal, 0, 0));
			}
			
			now = now.minusMonths(1);
		}
		
		return vendasNacionalidade;
	}
	

	private Predicate[] formatarFiltroPesquisa(VendaFilter filtro, CriteriaBuilder builder, Root<Venda> root,
			Join<Venda, Cliente> cliente) {
		List<Predicate> listaPredicates = new ArrayList<>();

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				listaPredicates.add(
						builder.like(builder.lower(cliente.get("nome")), "%" + filtro.getNomeCliente().toLowerCase() + "%")
						);
			}
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				listaPredicates.add(
						builder.equal(cliente.get("cpfOuCnpj"), TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente()))
						);
			}
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				listaPredicates.add(builder.equal(root.get("codigo"), filtro.getCodigo()));
			}
			if (filtro.getStatus() != null) {
				listaPredicates.add(builder.equal(root.get("status"), filtro.getStatus()));
			}
			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				listaPredicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), desde));
			}
			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				listaPredicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), ate));
			}
			if (filtro.getValorMinimo() != null) {
				listaPredicates.add(builder.greaterThanOrEqualTo(root.get("valorTotal"), filtro.getValorMinimo()));
			}
			if (filtro.getValorMaximo() != null) {
				listaPredicates.add(builder.lessThanOrEqualTo(root.get("valorTotal"), filtro.getValorMaximo()));
			}
		}
		Predicate[] predicates = listaPredicates.toArray(new Predicate[listaPredicates.size()]);
		return predicates;
	}
	
	private void adicionarRestricoesDePaginacao(Pageable pageable, TypedQuery<?> query) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	private Long qtdeTotalRegistros(VendaFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Venda> root = criteria.from(Venda.class);
		Join<Venda, Cliente> cliente = (Join) root.join("cliente");
		
		Predicate[] predicates = formatarFiltroPesquisa(filtro, builder, root, cliente);
		criteria.where(predicates);
				
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
