Brewer = Brewer || {};

Brewer.BotaoSubmit = (function() {
	
	function BotaoSubmit() {
		this.submitBtn = $('.js-submit-btn');
		this.formulario = $('.js-formulario-principal');
	}
	
	BotaoSubmit.prototype.iniciar = function() {
		this.submitBtn.on('click', onSubmit.bind(this));
	}
	
	function onSubmit(evento) {
		evento.preventDefault();	// para não executar o procedimemnto default do evento do link
		
		var botaoClicado = $(evento.target);
		var acao = botaoClicado.data('acao');
		
//		console.log('Ação', acao);
		
		var acaoInput = $('<input>');
		acaoInput.attr('name', acao);
		
		this.formulario.append(acaoInput);
		
//		console.log("==>",this.formulario);
		
		this.formulario.submit();
	}
	
	return BotaoSubmit
	
}());

$(function() {
	
	var botaoSubmit = new Brewer.BotaoSubmit();
	botaoSubmit.iniciar();
	
});