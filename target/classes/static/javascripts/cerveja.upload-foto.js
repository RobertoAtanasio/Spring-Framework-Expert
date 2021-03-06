var Brewer = Brewer || {};

Brewer.UploadFoto = (function() {
	
	function UploadFoto() {
		this.inputNomeFoto = $('input[name=foto]');
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		
		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html();
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			action: this.containerFotoCerveja.data('url-fotos'),
			complete: onUploadCompleto.bind(this),
			beforeSend: adicionarCsrfToken,
			loadstart: onLoadStart.bind(this)
			// obs.: com o .bind acima, a função onUploadCompleto tem acesso às variáveis da função UploadFoto
		}
		
		UIkit.uploadSelect($('#upload-select'), settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		// tratamento para quando ocorrer algum erro de validação para qua a foto permaneça aparecendo.
		if (this.inputNomeFoto.val()) {
			// os parâmetros são de acordo com o objeto que passa os valores. Ver FotoDTO.java
			// o call(this é para executar dentro do contexto que deu a mensagem de erro
//			onUploadCompleto.call(this, { nome:  this.inputNomeFoto.val(), contentType: this.inputContentType.val()});
			renderizarFoto.call(this, { 
						nome: this.inputNomeFoto.val(), 
						contentType: this.inputContentType.val(), 
						url: this.inputUrlFoto.val() 
					});
		}
	}
	
	function onLoadStart() {
		this.imgLoading.removeClass('hidden');
	}
	
	function onUploadCompleto(resposta) {
		console.log(">> onUploadCompleto");
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url);
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this, resposta);
	}
	
//	function onUploadCompleto(resposta) {
//		this.inputNomeFoto.val(resposta.nome);
//		this.inputContentType.val(resposta.contentType);
//		
//		this.uploadDrop.addClass('hidden');
//		
//		console.log("foto...", resposta.nome);
//		
//		var htmlFotoCerveja = this.template({foto: resposta.nome});
//		this.containerFotoCerveja.append(htmlFotoCerveja);
//		
//		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
//	}
	
	function renderizarFoto(resposta) {
		this.inputNomeFoto.val(resposta.nome);
		this.inputContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass('hidden');
		
		// esta parte se refere ao arquivo quando colocado na pasta temporária. Excluído.
//		var foto = "";
//		if (this.novaFoto.val() == 'true') {
//			foto = 'temp/';
//		}
//		foto += resposta.nome;
		
		console.log(">> url: " + resposta.url);
		
//		var htmlFotoCerveja = this.template({foto: foto});
		var htmlFotoCerveja = this.template({url: resposta.url});
		this.containerFotoCerveja.append(htmlFotoCerveja);
		
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto() {
		$('.js-foto-cerveja').remove();
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	//--- não precisa adionar este script porque no arquivo brewer.js já está definido a função Brewer.Security
	//	  que envia estes parâmetros toda vez que se executa uma chamada AJAX
	
	function adicionarCsrfToken(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();

$(function() {
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});
