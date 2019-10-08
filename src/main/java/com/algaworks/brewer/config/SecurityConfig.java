package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.algaworks.brewer.security.AppUserDetailsService;

//--- internamente a classe WebSecurityConfigurerAdapter já tem a anotação @Configuration
//@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)	// permite adicionar a anotação @PreAuthorize em CadastroVendaService
													// método 'cancelar' para incluir controle de quem vai cancelar
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
//		auth.inMemoryAuthentication()
//			.withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
	}
	
	//--- O parâmetro .antMatchers é para permitir que quaçquer arquivos a partir do diretório
	//	  layout fique autorizado o acesso sem passar pela autenticação. Ver exemplo do script do login.html abaixo
	// <link rel="stylesheet" type="text/css" th:href="@{/layout/stylesheets/vendors.min.css}"/>
	// <script th:src="@{/layout/javascripts/vendors.min.js}"></script>
	
	//--- O parâmetro .permitAll após o .loginPage é indicar que a chamada da página login não precisa de autenticação.
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//			.authorizeRequests()
//				.antMatchers("/layout/**").permitAll()
//				.antMatchers("/images/**").permitAll()
//				.anyRequest().authenticated()
//				.and()
//			.formLogin()
//				.loginPage("/login")
//				.permitAll()
//				.and()
//			.csrf().disable();
//	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")
			.antMatchers("/images/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
			.antMatchers("/cidades/nova").hasRole("CADASTRAR_CIDADE")
			.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO")
			.anyRequest().authenticated()
//			.anyRequest().denyAll()
			.and()
		.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
		.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.and()
//		.exceptionHandling()
//			.accessDeniedPage("/403")
//			.and()
		.sessionManagement()
			.invalidSessionUrl("/login");
		
//		.and()
//		.sessionManagement()
//			.maximumSessions(1)
//			.expiredUrl("/login")
		
//				.and()
//			.csrf().disable();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
