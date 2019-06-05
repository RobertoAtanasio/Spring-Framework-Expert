package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

public class ClassForErrorAttributeTagProcessor extends AbstractAttributeTagProcessor {

	private static final String NOME_ATRIBUTO = "classforerror";
	private static final int PRECEDENCIA = 1000;
	
	public ClassForErrorAttributeTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, NOME_ATRIBUTO, true, PRECEDENCIA, true);
		//System.out.println(">>> dialectPrefix: " + dialectPrefix);  // aqui mostrou o nome 'brewer' definido como dialeto
	}
	
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		
		// Esta classe substitui os parâmetros --> th:classappend="${#fields.hasErrors('nome')} ? has-error"
		// colocados no CadastroCerveja.html. Passando a ser chamado no .html da seguinte forma:
		// brewer:classforerror="nome"
		
		//System.out.println(">>> attributevalue: " + attributeValue);
		//System.out.println(">>> context: " + context);	// aqui é a página html passada
		
		boolean temErro = FieldUtils.hasErrors(context, attributeValue);
		
		if (temErro) {
			String classesExistentes = tag.getAttributeValue("class");	// classes existentes na div
			structureHandler.setAttribute("class", classesExistentes + " has-error"); // adicionar has-error às demais classes
		}
	}

}