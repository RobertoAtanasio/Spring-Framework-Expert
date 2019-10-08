package com.algaworks.brewer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

import com.algaworks.brewer.validation.AtributoConfirmacao;

//--- coloca-se esta anotação sobre a classe para poder ter acesso a todos os atributos da classe
//--- ver classe @AtributoConfirmacao.java
//--- Se não informar o parâmetro message abaixo, o sistema assume a mensagem default definida em AtributoConfirmacao.java
@AtributoConfirmacao(atributo = "senha", atributoConfirmacao = "confirmacaoSenha", message = "Confirmação da senha não confere")
//@AtributoConfirmacao(atributo = "senha", atributoConfirmacao = "confirmacaoSenha")
@Entity
@Table(name = "usuario")
@NamedQueries({
	@NamedQuery(name="Usuario.findAllUsuariosInner",query="SELECT u FROM Usuario u inner join fetch u.grupos"),
	@NamedQuery(name="Usuario.findUsuarioGrupo",query="SELECT u FROM Usuario u inner join fetch u.grupos"),
	@NamedQuery(name="Usuario.findUsuarioGrupoIn",query="SELECT u FROM Usuario u inner join fetch u.grupos g WHERE g.codigo IN (:codigos)")
})

@DynamicUpdate		// faz com que o update seja apenas nos campos alterados
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;

	private String senha;
	
	@Transient
	private String confirmacaoSenha;

	private Boolean ativo;

	//--- obs.: se a lista de array estiver vazia, o @NotNull não funciona, pois vazio não é Null.
//	@NotNull(message = "Selecione pelo menos um grupo")
	@Size(min=1, message = "Selecione pelo menos um grupo")
	@ManyToMany
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "codigo_usuario")
				, inverseJoinColumns = @JoinColumn(name = "codigo_grupo"))	
	private List<Grupo> grupos;

//	@NotNull(message = "Data de nascimento é obrigatório")
	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;

	@PreUpdate
	private void preUpdate() {
		this.confirmacaoSenha = senha;
	}
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	
	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public boolean isNovo() {
		return this.codigo == null;
	}
	
	public boolean isExisteGrupo() {
		if (this.getGrupos() == null) {
			return false;
		}
		return !this.getGrupos().isEmpty();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}