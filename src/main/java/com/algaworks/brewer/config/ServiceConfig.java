package com.algaworks.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.storage.FotoStorage;

@Configuration
@ComponentScan(basePackageClasses = { CadastroCervejaService.class, FotoStorage.class})
//@ComponentScan(basePackageClasses = {CadastroCervejaService.class})
public class ServiceConfig {

//	@Bean
//	public FotoStorage fotoStorage() {
//		return new FotoStorageLocal();
//	}
	
}
