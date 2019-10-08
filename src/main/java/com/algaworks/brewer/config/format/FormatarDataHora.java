package com.algaworks.brewer.config.format;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;

import org.springframework.format.Formatter;

public abstract class FormatarDataHora<T extends Temporal> implements Formatter<T>{

	public String print(T temporal, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern(locale));
		return formatter.format(temporal);
	}

	public T parse(String text, Locale locale) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern(locale));
		return parse(text, formatter);
	}
	
	public abstract String pattern(Locale locale);

	public abstract T parse(String text, DateTimeFormatter formatter);
	
}
