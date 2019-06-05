package com.algaworks.brewer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Teste;
import com.algaworks.brewer.repository.Testes;

@Service
public class TesteService {

	@Autowired
	private Testes testes;
	
	@Transactional
	public void salvar(Teste teste) {
		
		LocalDate dataAux = LocalDate.now();
		LocalTime hora = LocalTime.now();
		
		System.out.println(">>>> LocalDate: " + dataAux);
		System.out.println(">>>> LocalTime: " + hora);
		
		teste.setDataHora(LocalDateTime.of(dataAux, hora));
		
		System.out.println(">>>> Data Hora: " + teste.getDataHora());
		
		testes.save(teste);	
	}

	
}
