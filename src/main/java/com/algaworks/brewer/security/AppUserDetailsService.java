package com.algaworks.brewer.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private Usuarios usuarios;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Usuario> usuarioOptional = usuarios.porEmailEAtivo(email);
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));
		//--- o new HashSet<>() abaixo se refere às permissões que ainda serão tratadas
//		return new User(usuario.getEmail(), usuario.getSenha(), new HashSet<>());
//		return new User(usuario.getEmail(), usuario.getSenha(), getPermissoes(usuario));
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		List<String> permissoes = usuarios.permissoes(usuario);
		
		System.out.println(">>>>> " + permissoes);
		
		permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.toUpperCase())));
		
		//--- da forma abaixo, não é preciso incluir a string 'ROLE_' na permissão que foi gravada no banco de dados.
		//	  Mas a passagem do parâmetro deve ser .hasRole
		//    EX.: .antMatchers("/cidades/nova").hasRole("CADASTRAR_CIDADE")
//		permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority("ROLE" + p.toUpperCase())));
		
		return authorities;
	}
	
}
