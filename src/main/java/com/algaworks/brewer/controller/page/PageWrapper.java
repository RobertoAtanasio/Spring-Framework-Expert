package com.algaworks.brewer.controller.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

// Esta classe foi criada para permitir manter o filtro (url) quando da paginação na tela de pesquisa.
// Em vez de utilizar <tr th:each="cerveja : ${pagina.content}"> na página,
// utilizaremos <tr th:each="cerveja : ${pagina.conteudo}">		>>>> ver método public List<T> getConteudo () abaixo.

// Na tag <tr th:if="${#lists.isEmpty(pagina.conteudo)}"> na tela de pesquisa, mudar para <tr th:if="${pagina.vazia}">

// Na tag <div class="col-sm-12  text-center" th:if="${not #lists.isEmpty(pagina.conteudo)}"> na tela de pesquisa, mudar 
// para <tr th:if="${pagina.vazia}">

// Na tag <ul class="pagination  pagination-sm" th:with="paginaAtual=${pagina.number}">, pode-se também alterar para
// <ul class="pagination  pagination-sm" th:with="paginaAtual=${pagina.atual}">, caso queira, mas devido a simplicidade
// da expressão, poderia ficar da forma anterior. Também nesse caso, em vez de utilizar a variável 'paginaAtual', pode-se
// utilizar a variável 'pagina.atual'.

// Mesmo procedimento para a tag <li th:class="${pagina.first} ? disabled">

// Na tag <a th:href="|?page=${numeroPagina}|">[[${p}]]</a>, altera com a inclusão do método public String urlParaPagina(int pagina) {

public class PageWrapper <T> {

	private Page<T> page;
	private UriComponentsBuilder uriBuilder;

	public PageWrapper(Page<T> page, HttpServletRequest httpServletRequest) {
		this.page = page;
//		this.uriBuilder = ServletUriComponentsBuilder.fromRequest(httpServletRequest);
		// o procedimento abaixo dá replace na URL que está com um BUG quando cocatena um espaço. Na string
		// fica gerado com o sinal +. Exemplo: pesquisa pelo string 'com foto', fica 'com+foto' e isso está com BUG
		
		// a inclusão do parâmetro .replaceAll("excluido", "") faz com que a URL formatada quando se seleciona
		// a ordenação da pesquisa não leve o texto "excluido" junto!
		
		String httpUrl = httpServletRequest.getRequestURL().append(
				httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "")
				.toString().replaceAll("\\+", "%20").replaceAll("excluido", "");
		this.uriBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
	}
	
	public List<T> getConteudo () {
		return page.getContent();
	}
	
	public boolean isVazia() {
		return page.getContent().isEmpty();
	}
	
	public int getAtual() {
		return page.getNumber();
	}
	
	public boolean isPrimeira() {
		return page.isFirst();
	}
	
	public boolean isUltima() {
		return page.isLast();
	}
	
	public int getPaginaInicio() {
		if (page.getTotalPages() == 0) {
			return 0;
		}
		return 1;
	}
	
	public int getTotalPaginas() {
		return page.getTotalPages();
	}
	
	public String urlParaPagina(int pagina) {
		return uriBuilder.replaceQueryParam("page", pagina).build(true).encode().toUriString();
	}
	
	public String urlOrdenada(String propriedade) {
		UriComponentsBuilder uriBuilderOrder = UriComponentsBuilder
				.fromUriString(uriBuilder.build(true).encode().toUriString());
		
//		String direcao = inverterDirecao(propriedade);
//		String classificacao = propriedade + "," + direcao;
//		String valorSort = String.format("%s,%s", propriedade, direcao);
		String valorSort = String.format("%s,%s", propriedade, inverterDirecao(propriedade));
		
//		System.out.println(">>>>> propriedade: " + propriedade);
//		System.out.println(">>>>> inverterDirecao: " + direcao);
//		System.out.println(">>>>> string para o Sort: " + valorSort);
//		System.out.println(">>>>> classificação: " + classificacao);
		
		return uriBuilderOrder.replaceQueryParam("sort", valorSort).build(true).encode().toUriString();
//		return uriBuilderOrder.replaceQueryParam("sort", classificacao).build(true).encode().toUriString();
	}
	
	public String inverterDirecao(String propriedade) {
		String direcao = "asc";
		
		Order order = page.getSort() != null ? page.getSort().getOrderFor(propriedade) : null;
		if (order != null) {
			direcao = Sort.Direction.ASC.equals(order.getDirection()) ? "desc" : "asc";
		}
		
		return direcao;
	}
	
	public boolean descendente(String propriedade) {
		return inverterDirecao(propriedade).equals("asc");
	}
	
	public boolean ordenada(String propriedade) {
		Order order = page.getSort() != null ? page.getSort().getOrderFor(propriedade) : null; 
		
		if (order == null) {
			return false;
		}
		
		return page.getSort().getOrderFor(propriedade) != null ? true : false;
	}
}
