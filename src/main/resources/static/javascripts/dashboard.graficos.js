var Brewer = Brewer || {};

Brewer.GraficoVendaPorMes = (function() {
	
	// para maiores informações Acessar site: https://www.chartjs.org/docs/latest/getting-started/installation.html
	// O gráfico usado abaixo é o LineChart
	
	function GraficoVendaPorMes() {
		this.ctx = $('#graficoVendasPorMes')[0].getContext('2d');
	}
	
	GraficoVendaPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/totalPorMes',
			method: 'GET', 
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaMes) {

		var meses = [];
		var valores = [];
		
		vendaMes.forEach(function(obj) {
			meses.unshift(obj.mes);
			valores.unshift(obj.total);
		});
		
//		meses = vendaMes.map( data => data.mes);
//		valores = vendaMes.map( data => data.total);
//		console.log('>> vendaMes', vendaMes);
//		console.log('>> meses', meses);
//		console.log('>> valores', valores);

		var graficoVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Vendas por mês',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                pointBorderColor: "rgba(26,179,148,1)",
	                pointBackgroundColor: "#fff",
	                data: valores
		    	}]
		    },
		});
	}
	
	return GraficoVendaPorMes;
	
}());


Brewer.GraficoVendaPorOrigem = (function() {
	
	function GraficoVendaPorOrigem() {
		this.ctx = $('#graficoVendasPorOrigem')[0].getContext('2d');
	}
	
	GraficoVendaPorOrigem.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/porOrigem',
			method: 'GET', 
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaOrigem) {
		var meses = [];
		var cervejasNacionais = [];
		var cervejasInternacionais = [];
		
		vendaOrigem.forEach(function(obj) {
			meses.unshift(obj.mes);
			cervejasNacionais.unshift(obj.totalNacional);
			cervejasInternacionais.unshift(obj.totalInternacional)
		});
		
		var graficoVendasPorOrigem = new Chart(this.ctx, {
		    type: 'bar',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Nacional',
		    		backgroundColor: "rgba(0,255,0,0.5)",
	                data: cervejasNacionais
		    	},
		    	{
		    		label: 'Internacional',
		    		backgroundColor: "rgba(255,0,0,0.5)",
	                data: cervejasInternacionais
		    	}]
		    },
		});
	}
	
	return GraficoVendaPorOrigem;
	
}());


$(function() {
	var graficoVendaPorMes = new Brewer.GraficoVendaPorMes();
	graficoVendaPorMes.iniciar();
	
	var graficoVendaPorOrigem = new Brewer.GraficoVendaPorOrigem();
	graficoVendaPorOrigem.iniciar();
});