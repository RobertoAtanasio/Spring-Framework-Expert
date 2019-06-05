package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelasItensSession {

	// para sessão do mesmo usuário, podemos ter várias tabelas de itens
	// O set não permite repetições de mesma tabela de itens
	// Cada entrada neste Set representa uma nova aba de tabela de itens que o usuário por ventura possa abrir, 
	// nesta caso, substituir, atualizando com a tabela atualizada.
	
	private Set<TabelaItensVenda> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = this.buscarTabelaPorUuid(uuid);
		tabela.adicionarAtualizarItem(cerveja, quantidade);
		tabelas.add(tabela);
	}

	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = this.buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = this.buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
	}

	public List<ItemVenda> getItens(String uuid) {
		return this.buscarTabelaPorUuid(uuid).getItens();
	}
	
	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		// se não existir a tabela, será criada.
		TabelaItensVenda tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensVenda(uuid));
		return tabela;
	}

	public Object getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}
	
}