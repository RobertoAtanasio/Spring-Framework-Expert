package com.algaworks.brewer.repository.listener;

import javax.persistence.PostLoad;

import com.algaworks.brewer.BrewerApplication;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

//esta classe é ativada fora do contexto do spring. Ela é ativada pelo Hibernate
//por isso não é considerado um Bean spring. Logo o comando SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//que deveria "injetar" o @Autowired não funciona mais. 
//Criou-se o método public static <T> T getBean(Class<T> type) em BrewerApplication.java que fará a injeção da FotoStorage
//quando executado abaixo.

public class CervejaEntityListener {

//	@Autowired
//	private FotoStorage fotoStorage;
	
	@PostLoad
	public void postLoad(final Cerveja cerveja) {
//		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		FotoStorage fotoStorage = BrewerApplication.getBean(FotoStorage.class);
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}
	
}