package com.algaworks.brewer.config.format;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalDateFormatter extends FormatarDataHora<LocalDate>{

	@Autowired
	private Environment env;
	
	// o parâmetro "dd/MM/yyyy", abaixo, poderia ter definido em application.properties: localtime.format-pt_BR=dd/MM/yyyy
	// O terceiro parãmetro é o padrão, caso não existe o locale informado.
	@Override
	public String pattern(Locale locale) {
		return env.getProperty("localdate.format-" + locale, "dd/MM/yyyy");
	}
	
//	@Override
//	public String pattern(Locale locale) {
//		return "dd/MM/yyyy";
//	}

	@Override
	public LocalDate parse(String text, DateTimeFormatter formatter) {
		return LocalDate.parse(text, formatter);
	}

}

/*
@Component
public class LocalDateFormatter implements Formatter<LocalDate>{

	@Override
	public String print(LocalDate localDate, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return formatter.format(localDate);
	}

	@Override
	public LocalDate parse(String text, Locale locale) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDate.parse(text, formatter);
	}

}
*/