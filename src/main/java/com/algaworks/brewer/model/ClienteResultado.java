package com.algaworks.brewer.model;

public class ClienteResultado {

	private String nome;
	private String cpfCnpj;
	
	public ClienteResultado(String nome, String cpfCnpj) {
		this.nome = nome;
		this.cpfCnpj = cpfCnpj;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

}
