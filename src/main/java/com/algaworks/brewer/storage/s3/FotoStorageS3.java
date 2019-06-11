package com.algaworks.brewer.storage.s3;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);

	private static final String BUCKET = "aws-rapl";
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Override
	public String salvar(MultipartFile[] files) {
		
		System.out.println("<<<<<< FotoStorageS3/salvar");
		
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			String nomeOriginal = arquivo.getOriginalFilename();
			novoNome = renomearArquivo(nomeOriginal);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
			}
			try {
				// dá a permissão de acesso ao arquivo enviado
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				
				enviarFoto(novoNome, arquivo, acl);
				enviarThumbnail(novoNome, arquivo, acl);
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando arquivo no S3", e);
			}
		}
		
		return novoNome;
	}

	@Override
	public byte[] recuperar(String foto) {
		InputStream is = amazonS3.getObject(BUCKET, foto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuperar foto do S3", e);
		}
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(FotoStorage.THUMBNAIL_PREFIX + foto);
	}

	@Override
	public void excluir(String foto) {
		// com deleteObjects no plural, pose-se passar uma lista de fotos
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, THUMBNAIL_PREFIX + foto));
	}

	@Override
	public String getUrl(String foto) {
		if (!StringUtils.isEmpty(foto)) {
			return "https://aws-rapl.s3.amazonaws.com/" + foto;
			//https://aws-rapl.s3.amazonaws.com/cerveja-mock.png
			//https://aws-rapl.s3.amazonaws.com/thumbnail.cerveja-mock.png
		}
		return null;
	}
	
	private ObjectMetadata enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl)
			throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());
		// enviar para o S3
		amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
					.withAccessControlList(acl));
		return metadata;
	}

	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl)	throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// redimencionar o tamanho do arquivo 
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);
		// salva em um array de bytes
		byte[] array = os.toByteArray();
		// array de bytes para converter em um inputstream
		InputStream is = new ByteArrayInputStream(array);
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);
		// enviar para o S3
		amazonS3.putObject(new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
					.withAccessControlList(acl));
	}
}