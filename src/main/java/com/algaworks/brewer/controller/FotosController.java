package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

@RestController
@RequestMapping("/fotos")		// parâmetro formatado no javascript action: '/brewer/fotos' do settings
public class FotosController {

	// as duas anotações abaixo fazem a mesma coisa.
	
//	//@RequestMapping(method = RequestMethod.POST)
//	@PostMapping
//	public String upload(@RequestParam("files[]") MultipartFile[] files) {
//		System.out.println(">>> Tamanho arquivo: " + files[0].getSize());
//		return "Ok!";
//	}
	
	@Autowired
	private FotoStorage fotoStorage;
	
	// para inclusão do retorno assícrono substituiu o parâmetro String por DeferredResult<FotoDTO>
	
	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();

		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();
		
		return resultado;
	}

//	public byte[] recuperarFotoTemporaria(@PathVariable("nome") String nomeFoto) {
//  caso queira mudar o nome do parâmetro para nomeFoto
	@GetMapping("/temp/{nome:.*}")
	public byte[] recuperarFotoTemporaria(@PathVariable String nome) {
		return fotoStorage.recuperarFotoTemporaria(nome);
	}

	//--- o .* é uma expressão regular que recebe qualquer coisa após o ponto (.)
	@GetMapping("/{nome:.*}")
	public byte[] recuperar(@PathVariable String nome) {
		return fotoStorage.recuperar(nome);
	}
	
}