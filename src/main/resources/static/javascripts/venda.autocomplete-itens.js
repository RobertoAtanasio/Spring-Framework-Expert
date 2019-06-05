Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function() {
		//--- o parâmetro da função abaixo é passado pelo easy autocomplete
		//--- minCharNumber = só pesquisa a partir do terceiro caracter
		//--- com a alteração do métod public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) para
		//	  @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE) podemos retirar o parâmetro 'filtro'
		//--- Se não incluir o ajxSettings vai dá o erro WARNING: Fail to load response data
		//--- Ver http://easyautocomplete.com/guide#sec-delay. O parâmetro requestDelay é para os casos quando
		//	  se digita muito rápido e o sistema espera um tempo para acessar os dados. No nosso caso, foi colocado
		//	  300 milisegundos.
		//--- cerveja é o objeto Cerveja que retornou do banco
		var options = {
			url: function (skuOuNome) {
//				return '/brewer/cervejas/filtro?skuOuNome=' + skuOuNome
//			return '/brewer/cervejas?skuOuNome=' + skuOuNome
			return this.skuOuNomeInput.data('url') + '?skuOuNome=' + skuOuNome;
			}.bind(this),
			getValue: 'nome',
			minCharNumber: 3,
			ajaxSettings: {
				contentType: 'application/json'
			},
			requestDelay: 300,
			template: {
				type: 'custom',
				method: template.bind(this)
			},
			list: {
				onChooseEvent: onItemSelecionado.bind(this)
			}
		};
		
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	function onItemSelecionado() {
		// o método getSelectedItemData faz parte do autocomplete
//		console.log('item-selecionado', this.skuOuNomeInput.getSelectedItemData());
		this.emitter.trigger('item-selecionado', this.skuOuNomeInput.getSelectedItemData());
		this.skuOuNomeInput.val('');
		this.skuOuNomeInput.focus();
	}
	
	function template(nome, cerveja) {
//		console.log("Valor cerveja antes: " + cerveja.valor);
		cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
//		console.log("Valor cerveja depois: " + cerveja.valorFormatado);
		return this.template(cerveja);
	}
	
	return Autocomplete
	
}());

Brewer.TabelaItens = (function() {
	
	function TabelaItens(autocomplete) {
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer = $('.js-tabela-cervejas-container');
		this.uuid = $('#uuid').val();
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	TabelaItens.prototype.iniciar = function() {
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	
	TabelaItens.prototype.valorTotal = function() {
		return this.tabelaCervejasContainer.data('valor');
	}
	
	//--- o POST abaixo, chama em VendasController o método adicionarItem
	//    que retorna o ModelAndView com o item selecionado que está no TabelaItensVenda.html, 
	//    ou seja, irá retornar o html formatado que irá ser adicionado por this.tabelaCervejasContainer.html(html);
	
	function onItemSelecionado(evento, item) {
//		console.log('Item selecionado do autocomplete', item);
		var resposta = $.ajax({
			url: 'item',
			method: 'POST',
			data: {
				codigoCerveja: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
//		resposta.done(function(data) {
//			console.log('>>>> retorno:', data);
//		});
	}
	
	function onItemAtualizadoNoServidor(html) {
		this.tabelaCervejasContainer.html(html);
		
		bindQuantidade.call(this);
		
		var tabelaItem = bindTabelaItem.call(this); 
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total'));
		
		// o elemento abaixo só existe após a rederização da tela de itens, por isso foi colocado aqui
		// o evento change.
		
//		let quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
//		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
//		quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
//		
//		let tabelaItem = $('.js-tabela-item');
//		tabelaItem.on('dblclick', onDoubleClick);
//		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
//		
//		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total'));
		
//		$('.js-tabela-cerveja-quantidade-item').on('change', onQuantidadeItemAlterado.bind(this));
//		$('.js-tabela-item').on('dblclick', onDoubleClick);
//		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));		
		
	}
	
	function onQuantidadeItemAlterado(evento) {
		var input = $(evento.target);
		var quantidade = input.val();
		
		if (quantidade <= 0) {
			input.val(1);
			quantidade = 1;
		}
		
		var codigoCerveja = input.data('codigo-cerveja');	// esse nome está no html
		
//		console.log("quantidade:",quantidade);
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'PUT',
			data: {
				quantidade: quantidade,
				uuid: this.uuid
			}
		});
		
		// no done chamamos o método novamente para a recarga da lista de itens na tela
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento) {
		
		// o toggleClass fica alternando ora inclui a classe solicitando-exclusao ora exclui
		// apesar de que o toglleClass é efetuado sobre o elemento no qual se deu esse duplo clicked, como 
		// foi efetuado sob a div onde está a classe .js-tabela-item, é a div que vai "escultar" o evento
		
//		let item = $(evento.currentTarget);
//		item.toggleClass('solicitando-exclusao');
		
		// então em outras palavras, como quem escultou o eveto foi o this, podemos colocar este atalho:
		// Este this é o evento.currentTarget
		
		$(this).toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItemClick(evento) {
//		let input = $(evento.target);
//		var codigoCerveja = input.data('codigo-cerveja');
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
		var resposta = $.ajax({
			url: 'item/' + this.uuid + '/' + codigoCerveja,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function bindQuantidade() {
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
	}
	
	function bindTabelaItem() {
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		return tabelaItem;
	}
	
	return TabelaItens;
	
}());




//---------------------------------- a inicialização foi colocada no arquivo venda.js





//$(function() {
//	
//	var autocomplete = new Brewer.Autocomplete();
//	autocomplete.iniciar();
//	
//	var tabelaItens = new Brewer.TabelaItens(autocomplete);
//	tabelaItens.iniciar();
//	
//});

//--- o script abaixo foi transferido para o arquivo venda.tabela-itens.js

//$(function() {
//	
//	let autocomplete = new Brewer.Autocomplete();
//	autocomplete.iniciar();
//	
//})

//Brewer = Brewer || {};
//
//Brewer.Autocomplete = (function() {
//	
//	function Autocomplete() {
//		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
//		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
//		this.template = Handlebars.compile(htmlTemplateAutocomplete);
//	}
//	
//	Autocomplete.prototype.iniciar = function() {
//		var options = {
//			url: function(skuOuNome) {
//				return '/brewer/cervejas?skuOuNome=' + skuOuNome;
//			},
//			getValue: 'nome',
//			minCharNumber: 3,
//			requestDelay: 300,
//			ajaxSettings: {
//				contentType: 'application/json'
//			},
//			template: {
//				type: 'custom',
//				method: function(nome, cerveja) {
//					cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
//					return this.template(cerveja);
//				}.bind(this)
//			}
//		};
//		
//		this.skuOuNomeInput.easyAutocomplete(options);
//	}
//	
//	return Autocomplete
//	
//}());
//
//$(function() {
//	
//	let autocomplete = new Brewer.Autocomplete();
//	autocomplete.iniciar();
//	
//})