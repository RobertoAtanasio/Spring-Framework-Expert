var Brewer = Brewer || {};

Brewer.MascaraCpfCnpj = (function() {
	
	function MascaraCpfCnpj() {
		this.radioTipoPessoa = $('.js-radio-tipo-pessoa');
		this.labelCpfCnpj = $('[for=cpfOuCnpj]');	// acessar via a propriedade
		this.inputCpfCnpj = $('#cpfOuCnpj');
	}
	
	MascaraCpfCnpj.prototype.iniciar = function() {
		this.radioTipoPessoa.on('change', onTipoPessoaAlterado.bind(this));
		var tipoPessoaSelecionada = this.radioTipoPessoa.filter(':checked')[0];
		if (tipoPessoaSelecionada) {
			aplicarMascara.call(this, $(tipoPessoaSelecionada));
		}
	}
	
	function onTipoPessoaAlterado(evento) {
//		console.log(evento);
		var tipoPessoaSelecionada = $(evento.currentTarget);
		aplicarMascara.call(this, tipoPessoaSelecionada);
		this.inputCpfCnpj.val('');
	}
	
	function aplicarMascara(tipoPessoaSelecionada) {
		/* Este tipo de formatação no html data-documento="CPF" data-mascara="000.000.000-00" é formatado no DOM
		 * no parâmetro abaixo:
		dataset: DOMStringMap
		documento: "CNPJ"
		mascara: "00.000.000/0000-00"
		*/
//		// o parâmetro do html data-documento="CPF" é o que está associado a .data('documento') abaixo.
//		console.log("evento: ", evento);
//		console.log("tipoPessoaSelecionada: ", tipoPessoaSelecionada);
//		console.log("Documento: ", tipoPessoaSelecionada.data('documento'));
		this.labelCpfCnpj.text(tipoPessoaSelecionada.data('documento'));
		this.inputCpfCnpj.mask(tipoPessoaSelecionada.data('mascara'));
		this.inputCpfCnpj.removeAttr('disabled');
	}
	
	return MascaraCpfCnpj;
	
}());

$(function() {
	var mascaraCpfCnpj = new Brewer.MascaraCpfCnpj();
	mascaraCpfCnpj.iniciar();
});