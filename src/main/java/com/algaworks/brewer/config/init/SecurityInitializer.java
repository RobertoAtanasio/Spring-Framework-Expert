package com.algaworks.brewer.config.init;

import java.util.EnumSet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	//--- o código abaixo faz com que a acentuação volte a funcionar após a inclusão do módulo de segurança .csrf()
	//	  ver classe AppInitializer.java e SecurityConfig.java
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		
//		servletContext.getSessionCookieConfig().setMaxAge(20);	// tempo em segundos
		
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));	// dessa formar ver parâmetro session-timeout
																						// em web.xml
		
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",
				new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
	}
	
}