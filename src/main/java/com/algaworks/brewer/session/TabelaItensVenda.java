package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

//--- o @Component transforma a classe em um bean 
//--- o @SessionScope cria uma sessão do componente TabelaItensVenda por usuário logado.
//@SessionScope
//@Component
class TabelaItensVenda {

	private String uuid;
	private List<ItemVenda> itens = new ArrayList<>();
	
	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	}
	
	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarAtualizarItem(Cerveja cerveja, Integer quantidade) {
		
//		System.out.println(">>> quantidade: " + quantidade);
		
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
//			System.out.println(">>> quantidade: " + quantidade + " | " + itemVenda.getQuantidade());
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			itens.add(0, itemVenda);	// o parâmetro 0 faz com que o item selecionado fique no topo da lista.
		}
	}

	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}	
	
	public void excluirItem(Cerveja cerveja) {
		
		// pesquisar o índe onde está a cerveja e remover pelo índice.
		// pesquisa dentro dos índice entre 0 e itens.size e filtra comparando o objeto cerveja
		// e obtém o índice encontrato.
		
		// uma outra forma seria utilizar um for de 0 ao size e comparar o objeto. Encontrando, dá um break e sai do loop
		
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				.findAny().getAsInt();
		itens.remove(indice);
	}
	
	public int total() {
		return itens.size();
	}
	
	public List<ItemVenda> getItens() {
		return itens;
	}

	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}
	
	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
