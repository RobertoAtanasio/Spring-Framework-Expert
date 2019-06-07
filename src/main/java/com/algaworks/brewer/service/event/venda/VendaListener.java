package com.algaworks.brewer.service.event.venda;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.repository.Cervejas;

@Component
public class VendaListener {

	@Autowired
	private Cervejas cervejas;
	
	@EventListener
	public void vendaEmitida(VendaEvent vendaEvent) {
		for (ItemVenda item : vendaEvent.getVenda().getItens()) {
			Optional<Cerveja> cerveja = cervejas.findById(item.getCerveja().getCodigo());
			cerveja.get().setQuantidadeEstoque(cerveja.get().getQuantidadeEstoque() - item.getQuantidade());
			cervejas.save(cerveja.get());
		}
	}
}
