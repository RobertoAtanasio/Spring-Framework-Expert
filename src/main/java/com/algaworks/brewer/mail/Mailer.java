package com.algaworks.brewer.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

@Component
public class Mailer {

	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Async
	public void enviar(Venda venda) {
		
		Context context = new Context(new Locale("pt", "BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo"); // definido no html como th:src="|cid:${logo}|". o cid diz que é para obter do conteúdo do email
					// ver abaixo no helper.addInline a definição da passagem da imagem
		
		Map<String, String> fotos = new HashMap<>();	// para salvar as fotos dos itens
		boolean adicionarMockCerveja = false;
		for (ItemVenda item : venda.getItens()) {
			Cerveja cerveja = item.getCerveja();
			if (cerveja.temFoto()) {
				String cid = "foto-" + cerveja.getCodigo();
				context.setVariable(cid, cid);

				fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());	// * ver abaixo o uso em String foto = fotoContentType[0]; por exemplo
			} else {
				adicionarMockCerveja = true;
				context.setVariable("mockCerveja", "mockCerveja");
			}
		}
		
		try {
			String email = thymeleaf.process("mail/ResumoVenda", context);
			
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");	// o true é porque vai-se adicionar imagens
			helper.setFrom("roberto.atanasio.pl@gmail.com");
			helper.setTo(venda.getCliente().getEmail());
			// o String.format abaixo faz com que o código seja passado para a variável 'd'
			helper.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));
			helper.setText(email, true);
			
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));
			
			if (adicionarMockCerveja) {
				helper.addInline("mockCerveja", new ClassPathResource("static/images/cerveja-mock.png"));
			}
			
			for (String cid : fotos.keySet()) {
				String[] fotoContentType = fotos.get(cid).split("\\|");	// para não interpretar como 'ou' faz-se a expressão regular colocando-se as \\
				String foto = fotoContentType[0];
				String contentType = fotoContentType[1];
//				System.out.println("foto: " + cid + " | "+ foto);
				byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);
				helper.addInline(cid, new ByteArrayResource(arrayFoto), contentType);
			}
		
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			// obs.: aqui poderia ser gravado no BD alguma informação sobre erro no envio de email, etc.
			logger.error("Erro enviando e-mail", e);
		}
	}
	
	
//	@Async		// alterado também o WebConfig.java para injetar o @EnableAsync
//	public void enviar(Venda venda) {
//		System.out.println("====>>> Enviando email ......");
////		try {
////			Thread.sleep(10000);
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
//		
//		SimpleMailMessage mensagem = new SimpleMailMessage();
//		mensagem.setFrom("roberto.atanasio.pl@gmail.com");
//		
//		System.out.println("====>>> Email: " + venda.getCliente().getEmail());
//		
//		mensagem.setTo(venda.getCliente().getEmail());
//		mensagem.setSubject("Venda Efetuada");
//		mensagem.setText("Obrigado, sua venda foi processada!");
//		
//		mailSender.send(mensagem);
//
//		System.out.println("====>>> Email enviado.");
//	}
	
	
}