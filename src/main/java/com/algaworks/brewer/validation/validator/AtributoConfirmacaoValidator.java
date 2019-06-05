package com.algaworks.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import com.algaworks.brewer.validation.AtributoConfirmacao;

//--- por exemplo: o parâmetro Object abaixo se refere ap objeto Usuario, no qual está definido em @AtributoConfirmacao
//--- O nome do parâmetro em vez de ser Object, poderia ser o objeto Usuario. Neste caso, deve-se renomear todas as
//	  referência desse objeto dentro da classe abaixo.

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	//--- Estas duas strings abaixo são os parâmetros da notação 
	//	  @AtributoConfirmacao(atributo = "senha", atributoConfirmacao = "confirmacaoSenha",...
	//	  definidas na classe Usuario.java
	
	private String atributo;
	private String atributoConfirmacao;
	
	@Override
	public void initialize(AtributoConfirmacao atributoConfirmacao) {
		//--- são as variáveis definidas na classe AtributoConfirmacao.java
		//	  Contêm os valores dos parâmeros atributo e atributoConfirmacao, que são: "senha" e "confirmacaoSenha"
		this.atributo = atributoConfirmacao.atributo();
		this.atributoConfirmacao = atributoConfirmacao.atributoConfirmacao();
		
//		System.out.println(">>>> Valores A: " + this.atributo + " / " + this.atributoConfirmacao);
		
	}
	
	//--- obs.: por exemplo, Object abaixo é = Usuario. Que é o mesmo objeto definido acima na implements da classe
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean valido = false;
		try {
			//--- Objecto BeanUtils configurado em pom.xml, em <!-- Apache Bean Utils -->
			//	  contém os valores digitados da senha e confirmacaoSenha.
			Object valorAtributo = BeanUtils.getProperty(object, this.atributo);
			Object valorAtributoConfirmacao = BeanUtils.getProperty(object, this.atributoConfirmacao);
			
//			System.out.println(">>>> Valores digitados: " + valorAtributo + " / " + valorAtributoConfirmacao);
			
			valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao) || ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);
		} catch (Exception e) {
			throw new RuntimeException("Erro recuperando valores dos atributos", e);
		}
		
		if (!valido) {
			//--- o bloco abaixo é para indicar que o atributo que está com erro é o atributoConfirmacao, que a confirmacaoSenha
			context.disableDefaultConstraintViolation();	// se não desabilitar, a amensagem de erro aparece duplicada
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}
}
