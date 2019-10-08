package com.algaworks.brewer.config.format;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalFormatter extends FormatarNumero<BigDecimal> {

	@Autowired
	private Environment env;
	
	// o parâmetro "#,##0.00", abaixo, poderia ter definido em application.properties: bigdecimal.format=#,##0.00
	// O terceiro parãmetro é o padrão, caso não existe o formato informdo no application.properties.
	@Override
	public String pattern(Locale locale) {
		return env.getProperty("bigdecimal.format", "#,##0.00");
	}

	

}