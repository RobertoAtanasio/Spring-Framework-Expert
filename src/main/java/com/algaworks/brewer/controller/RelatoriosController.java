package com.algaworks.brewer.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.dto.PeriodoRelatorio;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

	@GetMapping("/vendasEmitidas")
	public ModelAndView relatorioVendasEmitidas() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioVendasEmitidas");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}
	
	@PostMapping("/vendasEmitidas")
	public ModelAndView gerarRelatorioVendasEmitidas(PeriodoRelatorio periodoRelatorio) {
		
		// converter a data de LocalDateTime para tipo Date, pois o jasper report está definido como Date
		
		Date dataInicio = Date.from(LocalDateTime.of(periodoRelatorio.getDataInicio(), LocalTime.of(0, 0, 0))
				.atZone(ZoneId.systemDefault()).toInstant());
		Date dataFim = Date.from(LocalDateTime.of(periodoRelatorio.getDataFim(), LocalTime.of(23, 59, 59))
				.atZone(ZoneId.systemDefault()).toInstant());
		
		// parâmetros
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		parametros.put("data_inicio", dataInicio);	// "data_inicio" = ao definido no jasper report
		parametros.put("data_fim", dataFim);
		
		// nome do relatório criado no jasperreport: relatorio_vendas_emitidas.jasper
		return new ModelAndView("relatorio_vendas_emitidas", parametros);
	}
}
