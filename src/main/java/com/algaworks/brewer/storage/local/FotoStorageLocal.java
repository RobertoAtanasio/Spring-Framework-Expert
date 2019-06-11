package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
//	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	private Path local;
//	private Path localTemporario;
	
	public FotoStorageLocal() {		
//		this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
		this(getDefault().getPath("local", ".brewerfotos"));
	}
	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}

//	@Override
//	public String salvarTemporariamente(MultipartFile[] files) {
//		
//		String novoNome = null;
//		if (files != null && files.length > 0) {
//			MultipartFile arquivo = files[0];
//			novoNome = renomearArquivo(arquivo.getOriginalFilename());
//			try {
//				arquivo.transferTo(new File(this.localTemporario.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
//			} catch (IOException e) {
//				throw new RuntimeException("Erro salvando a foto na pasta temporária", e);
//			}
//		}
//		
//		return novoNome;
//	}
	
//	@Override
//	public byte[] recuperarFotoTemporaria(String nome) {
//		try {
//			// http://localhost:8080/brewer/fotos/temp/thumbnail.cerveja-mock.png
//			
////			System.out.println(">>>> NOME: " + nome);
////			System.out.println(">>>> FOTO: " + Files.readAllBytes(this.localTemporario.resolve(nome)));
//			
//			return Files.readAllBytes(this.localTemporario.resolve(nome));
//		} catch (IOException e) {
//			throw new RuntimeException("Erro lendo a foto temporária", e);
//		}
//	}
	
//	@Override
//	public void salvar(String foto) {
//		try {
//			Files.move(this.localTemporario.resolve(foto), this.local.resolve(foto));
//		} catch (IOException e) {
//			throw new RuntimeException("Erro movendo a foto para destino final", e);
//		}
//		
//		try {
//			Thumbnails.of(this.local.resolve(foto).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
//		} catch (IOException e) {
//			throw new RuntimeException("Erro gerando thumbnail", e);
//		}
//	}
	
	@Override
	public String salvar(MultipartFile[] files) {

		System.out.println("<<<<<< FotoStorageLocal/salvar");
		
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}
		
		return novoNome;
	}
	
	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			logger.warn(String.format("Erro apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));
		}
		
	}
	
	@Override
	public String getUrl(String foto) {
		return "http://localhost:8080/brewer/fotos/" + foto;
	}
	
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}
	
//	private void criarPastas() {
//		try {
//			
//			System.out.println(">>> this.local.toString(): " + this.local.toString());
//			
//			Files.createDirectories(this.local);
//			this.localTemporario = getDefault().getPath(this.local.toString(), "temp");
//			Files.createDirectories(this.localTemporario);
//			
//			if (logger.isDebugEnabled()) {
//				logger.debug("Pastas criadas para salvar fotos.");
//				logger.debug("Pasta default: " + this.local.toAbsolutePath());
//				logger.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());
//			}
//		} catch (IOException e) {
//			throw new RuntimeException("Erro criando pasta para salvar foto", e);
//		}
//	}
	
//	private String renomearArquivo(String nomeOriginal) {
//		// gerar um string randômico para concatenar ao nome da foto a fim de evitar duplicidade e replace
//		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
//		}
//		
//		return novoNome;
//		
//	}

}