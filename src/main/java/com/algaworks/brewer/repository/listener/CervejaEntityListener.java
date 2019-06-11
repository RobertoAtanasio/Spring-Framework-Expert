package com.algaworks.brewer.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

public class CervejaEntityListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	// o final vai fazer com que em nosso der erro se fizermos algo do tipo cerveja = null
	@PostLoad
	public void postLoad(final Cerveja cerveja) {
		// o comando abaixo vai revolver a injeção de dependência baseado no contexto corrente
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}
}
