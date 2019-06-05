package com.algaworks.brewer.service.exception;

public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessDeniedException(String mensagem) {
		super(mensagem);
	}
}
