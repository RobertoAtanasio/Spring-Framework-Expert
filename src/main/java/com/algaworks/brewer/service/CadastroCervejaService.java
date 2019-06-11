package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {

	// obs: é preciso criar uma entrada de configuração no AppInitializer.java para esse serviço.
	// No AppInitializer, associa-se a classe ServiceConfig.java, que precisamos criar e onde é
	// configurado esta classe 'CadastroCervejaService' de serviço. Ver método getRootConfigClasses().
	// No 'CervejasController' é injetado esse serviço para a atualização do banco.
	
	// injetar o repositório de cervejas para a atualização no bando de dados
	@Autowired
	private Cervejas cervejas;
	
//	@Autowired
//	private ApplicationEventPublisher publisher;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	// Para o parâmetro @Transactional funcionar corretamente é precido configurar a classe JPAConfig.java e incluir
	// os parâmetros enableDefaultTransactions=false e @EnableTransactionManagement.
	// Este parâmetro inicia o begin e end transaction, commitando o banco no final
	
	@Transactional
	public void salvar(Cerveja cerveja) {
		//cerveja.setSku(cerveja.getSku().toUpperCase());		--> esta passagem para upper foi colocada na própria entidade
		// da tabela Cerjeva.java
		cervejas.save(cerveja);
		
		// quando salvar uma cerveja, vamos lançar (publicar) um evento.
		// Normalmente colocamos ações que devem ser efetivadas somente após determinada coisa acontecer. Em vez
		// de incluir no objeto que salva as alterações no banco de dados, colocamos em um listenner
		
//		publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}
	
	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja);
			cervejas.flush();	// faz com a excessão seja lançada se houve erro no banco de dados
			fotoStorage.excluir(foto);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda.");
		}
	}
	
}
