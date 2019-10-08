package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.service.event.venda.VendaEvent;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Transactional
	public Venda salvar(Venda venda) {
		
		if (venda.isSalvarProibido()) {
			throw new RuntimeException("Usuário tentando salvar uma venda proibida");
		}
		
		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		} else {
			Venda vendaExistente = vendas.getOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		
//		BigDecimal valorTotalItens = venda.getItens().stream()
//				.map(ItemVenda::getValorTotal)
//				.reduce(BigDecimal::add)
//				.get();
//		
//		
//		BigDecimal valorTotal = calcularValorTotal(valorTotalItens, venda.getValorFrete(), venda.getValorDesconto());
//		
//		venda.setValorTotal(valorTotal);
		
//		System.out.println(">>>> Data da Entrega...: " + venda.getDataEntrega());
//		System.out.println(">>>> Hora da Entrega...: " + venda.getHorarioEntrega());
//		System.out.println(">>>> Data entrega formatada...: " + LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega()));

//		if (venda.getDataEntrega() != null) {
//			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega()));
//		}
		
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega()
					, venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}
		
//		vendas.save(venda);
		return vendas.saveAndFlush(venda);	// atualiza e dá um refresh no objeto
		
	}

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		this.salvar(venda);
		publisher.publishEvent(new VendaEvent(venda));
	}

	// @PreAuthorize --> regra de tem pode salvar 
	// obs.: principal.usuario é o 'getUsuario' em UsuarioSistema.java
	// o # representa o elemento Venda do parâmetr do método 'cancelar'
	// é preciso incluir <input type="hidden" th:field="*{usuario}"/> na tela e CadastroVenda.html
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.getOne(venda.getCodigo());
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		this.salvar(vendaExistente);
	}

//	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
//		BigDecimal valorTotal = valorTotalItens
//				.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO))
//				.subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO));
//		return valorTotal;
//	}
	
}