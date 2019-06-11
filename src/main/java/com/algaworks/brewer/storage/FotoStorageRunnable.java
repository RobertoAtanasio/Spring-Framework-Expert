package com.algaworks.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable {

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable(MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	}

	@Override
	public void run() {
//		String novoNome = this.fotoStorage.salvarTemporariamente(files);
		String nomeFoto = this.fotoStorage.salvar(files);
//		System.out.println(">>> files: " + files[0].getSize());
		// TODO: Salvar a foto no sistema de arquivos...
//		String nomeFoto = files[0].getOriginalFilename();
		String contentType = files[0].getContentType();
//		resultado.setResult("OK! Foto recebida!");
//		resultado.setResult(new FotoDTO(nomeFoto, contentType));
		resultado.setResult(new FotoDTO(nomeFoto, contentType, fotoStorage.getUrl(nomeFoto)));
	}

}