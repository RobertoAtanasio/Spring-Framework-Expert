Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	function MultiSelecao() {
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao');
		this.selecaoTodosCheckbox = $('.js-selecao-todos');
	}
	
	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosCheckbox.on('click', onSelecaoTodosClicado.bind(this));
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
	}
	
	function onStatusBtnClicado(event) {
		let botaoClicado = $(event.currentTarget);
		
//		console.log("Botão clicado: ", botaoClicado.data('status'));
//		console.log("Botão clicado: ", botaoClicado.context.name);
		
		let status = botaoClicado.data('status');	// obtem a partir de: data:status que está no html
		let url = botaoClicado.data('url');
		
		let checkBoxSelecionados = this.selecaoCheckbox.filter(':checked');	// devolve os selecionados
		
//		console.log("Lista dos selecionados: ", checkBoxSelecionados);
		
		let codigos = $.map(checkBoxSelecionados, function(c) {
			return $(c).data('codigo');
		});
		
//		console.log("Códigos: ", codigos );
//		console.log("Código tamanho: ", codigos.length );
		
		//--- o reload recarrega a tela para apresentar a alteração efetuada
		
		if (codigos.length > 0) {
			$.ajax({
//				url: '/brewer/usuarios/status',
				url: url,
				method: 'PUT',
				data: 
				{ 
					'codigosJSON': codigos,
					'status': status
				}, 
				success: function() {
					window.location.reload();
				}
			});
			
		}
	}
	
	function onSelecaoTodosClicado () {
		let status = this.selecaoTodosCheckbox.prop('checked');
//		console.log('status', status);
		this.selecaoCheckbox.prop('checked', status);
		statusBotaoAcao.call(this, status);
	}
	
	function onSelecaoClicado () {
		let selecaoCheckboxChecados = this.selecaoCheckbox.filter(':checked');
		this.selecaoTodosCheckbox.prop('checked', selecaoCheckboxChecados.length >= this.selecaoCheckbox.length);
		statusBotaoAcao.call(this, selecaoCheckboxChecados.length);
	}
	
	function statusBotaoAcao(ativar) {
		//--- o valor zero equivale a false. Maior que zero, true
		ativar ? this.statusBtn.removeClass('disabled') : this.statusBtn.addClass('disabled');
	}
	
	return MultiSelecao;
	
}());

$(function() {
	let multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});