package com.algaworks.brewer.config.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeFormatter extends FormatarDataHora<LocalDateTime>{

	@Autowired
	private Environment env;
	
	// o parâmetro "dd/MM/yyyy HH:mm", abaixo, poderia ter definido em application.properties: localdatetime.format-pt_BR=dd/MM/yyyy HH:mm
	// O terceiro parãmetro é o padrão, caso não existe o locale informado.
	@Override
	public String pattern(Locale locale) {
		return env.getProperty("localdatetime.format-" + locale, "dd/MM/yyyy HH:mm");
	}
	
//	@Override
//	public String pattern(Locale locale) {
//		return "dd/MM/yyyy HH:mm";
//	}

	@Override
	public LocalDateTime parse(String text, DateTimeFormatter formatter) {
		return LocalDateTime.parse(text, formatter);
	}

}

/*
@Component
public class LocalDateTimeFormatter implements Formatter<LocalDateTime>{

	@Override
	public String print(LocalDateTime localDateTime, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return formatter.format(localDateTime);
	}

	@Override
	public LocalDateTime parse(String text, Locale locale) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return LocalDateTime.parse(text, formatter);
	}

}
*/