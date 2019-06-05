package com.algaworks.brewer.dto;

public class VendaOrigemDTO {

	private String mes;
	private Integer totalNacional;
	private Integer totalInternacional;
	
	public VendaOrigemDTO() {
	}

	public VendaOrigemDTO(String mes, Integer totalNacional, Integer totalInternacional) {
		this.mes = mes;
		this.totalNacional = totalNacional;
		this.totalInternacional = totalInternacional;
	}

	public String getMes() {
		return mes;
	}

	public Integer getTotalNacional() {
		return totalNacional;
	}

	public void setTotalNacional(Integer totalNacional) {
		this.totalNacional = totalNacional;
	}

	public Integer getTotalInternacional() {
		return totalInternacional;
	}

	public void setTotalInternacional(Integer totalInternacional) {
		this.totalInternacional = totalInternacional;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}
}
