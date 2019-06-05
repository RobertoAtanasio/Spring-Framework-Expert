package com.algaworks.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.storage.FotoStorage;

@Component
public class CervejaListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	// só chamará o evento listener se ocorrer esta condição. Chamando o método definido em CervejaSalvaEvent
	@EventListener(condition = "#evento.temFoto() and #evento.novaFoto")
	public void cervejaSalva(CervejaSalvaEvent evento) {
		fotoStorage.salvar(evento.getCerveja().getFoto());
	}
	
}