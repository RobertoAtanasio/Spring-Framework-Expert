package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

@Service
public class CadastroEstiloService {

	@Autowired
	private Estilos estilos;
	
	@Transactional
	public Estilo salvar(Estilo estilo) {
		
		Optional<Estilo> estiloOptional = estilos.findByNomeIgnoreCase(estilo.getNome());
		if (estiloOptional.isPresent() && estilo.isNovo()) {
			throw new NomeEstiloJaCadastradoException ("Nome do estilo já cadastrado");
		}
		
		//estilos.save(estilo);
		return estilos.saveAndFlush(estilo);		// salva e popula o código do objeto com o valor gerado pelo banco de dados
	}
	
	@Transactional
	public void excluir(Estilo estilo) {
		
		try {
			Estilo estiloLido = estilos.findByCodigo(estilo.getCodigo());
			if (estiloLido.isExisteCerveja()) {
				throw new ImpossivelExcluirEntidadeException("Este estilo já está associado a uma cerveja. Exclusão não permitida");
			}
			estilos.delete(estilo);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda.");
		}
		
		return;
	}
}
