	package com.algaworks.brewer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SegurancaController {

	@GetMapping("/login")
	public String login(@AuthenticationPrincipal User user) {
		//--- o @AuthenticationPrincipal User user só injeta o usuário logado
		if (user != null) {
			// se existir um usuário já logado, redireciona para uma página qualquer. Nesse caso, usamos a página de cervejas
			return "redirect:/cervejas";
		}
		
		return "Login";
	}
	
	@GetMapping("/403")
	public String acessoNegado() {
		return "403";
	}
}