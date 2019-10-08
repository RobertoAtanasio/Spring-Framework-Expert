package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class MessageElementTagProcessor extends AbstractElementTagProcessor {

	private static final String NOME_TAG = "message";
	private static final int PRECEDENCIA = 1000;
	
	public MessageElementTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, NOME_TAG, true, null, false, PRECEDENCIA);
	}
	
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		
		IModelFactory modelFactory = context.getModelFactory();
		IModel model = modelFactory.createModel();
		
		// o parâmetro :: alert faz com que apresente apenas o html definido pelo th:fragment="alert" do
		// MensagensErroValidacao.html e MensagemSucesso.html e com a opção th:replace
		
		/*
		 <th:block th:include="fragments/MensagemSucesso"></th:block>
		 <th:block th:include="fragments/MensagensErroValidacao"></th:block>
		 */
		model.add(modelFactory.createStandaloneElementTag("th:block", "th:replace", "fragments/MensagemSucesso :: alert"));
		model.add(modelFactory.createStandaloneElementTag("th:block", "th:replace", "fragments/MensagensErroValidacao :: alert"));
		
		// Esta classe substitui as tag abaixo via chamada no html por <brewer:message/>
		// <th:block th:include="fragments/MensagemSucesso"></th:block>
		// <th:block th:include="fragments/MensagensErroValidacao"></th:block>
		
		structureHandler.replaceWith(model, true);
	}

}