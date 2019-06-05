// obs.: para maiores detalhes das mensagens, acessar o site https://sweetalert2.github.io/

Brewer = Brewer || {};

Brewer.DialogoExcluir = (function() {
	
	function DialogoExcluir() {
		this.exclusaoBtn = $('.js-exclusao-btn')
	}
	
	DialogoExcluir.prototype.iniciar = function() {
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
		
//		if (window.location.search.indexOf('excluido') > -1) {
//			Swal.fire({
//				  position: 'top-end',
//				  type: 'success',
//				  title: 'Registro Excluído com Sucesso!',
//				  showConfirmButton: false,
//				  timer: 2000
//				})			
//		}
	};
	
	function onExcluirClicado(evento) {
		event.preventDefault();
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');
		
//		console.log("url", url);
//		console.log("objeto", objeto);
		
		Swal.fire({
			  title: 'Tem certeza?',
			  text: 'Excluir "' + objeto + '? Você não poderá recuperá-lo depois.',
			  type: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'Sim, exclua-o!'
			}).then((result) => {
			  if (result.value) {
				onExcluirConfirmado(url);
			  }
			})
	};
	
	function onExcluirConfirmado(url) {
		$.ajax({
			url: url,
			method: 'DELETE',
			success: onExcluidoSucesso.bind(this),
			error: onErroExcluir.bind(this)
		});
	};
	
	function onExcluidoSucesso() {
//		window.location.reload;
		var urlAtual = window.location.href;
//		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
//		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';
	    Swal.fire(
	      'Excluído!',
	      'Registro Excluído com Sucesso!',
	      'success'
	    ).then( () => {
	    	window.location = urlAtual;
	    })
	};
	
	function onErroExcluir(e) {
		Swal.fire({
			  type: 'error',
			  title: 'Oops...',
			  text: e.responseText
			})
	};
	
	return DialogoExcluir;
	
}());

$(function() {
	var dialogo = new Brewer.DialogoExcluir();
	dialogo.iniciar();
});