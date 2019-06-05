package com.algaworks.brewer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

// Nas classes @Embeddable não é preciso criar o hashCode e equal

@Embeddable
public class Endereco implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="logradouro")
	private String logradouro;
	
	@Column(name="numero")
	private String numero;
	
	@Column(name="complemento")
	private String complemento;
	
	@Column(name="cep")
	private String cep;
	
	// Esye objeto Cidade vai precisar de um converter. Da mesma forma que foi feita em Estilo
	@ManyToOne
	@JoinColumn(name = "codigo_cidade", referencedColumnName="codigo")
	private Cidade cidade;
	
	// Este objeto Estado vai precisar de um converter. Da mesma forma que foi feita em Estilo
	// Quando tem o atributo @Transient a informação não vai para o banco de dados. Só está sendo colocado
	// aqui para levar a informação para o html
	@Transient
	private Estado estado;

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public String getNomeCidadeSiglaEstado() {
		
//		if (this.cidade != null) {
//			return this.cidade.getNome() + "/" + this.cidade.getEstado().getSigla();
//		} else {
//			if (this.getEstado() != null) {
//				return this.cidade.getEstado().getSigla();
//			} else {
//				return null;
//			}
//		}
		
		if (this.cidade != null) {
			return this.cidade.getNome() + "/" + this.cidade.getEstado().getSigla();
		}
		
		return null;
		
	}

}