package com.algaworks.brewer.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.NomeCidadeJaCadastradaException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	@RequestMapping("/nova")
	public ModelAndView nova(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());
		return mv;
	}
	
	@RequestMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo) {
		
		Cidade cidade = cidades.findOne(codigo);
		ModelAndView mv = this.nova(cidade);
		mv.addObject(cidade);
		return mv;
	}
	
	// exemplo: .../brewer/cidades?estado=2
	// O parâmetro de retorno do método (@ResponseBody) é para retornar como JSON
	// O parâmetro default = -1 abaixo serve para pesquisar com codigoEstado = -1, caso não seja informado nenhum Estado.
	
//	@Cacheable("cidades")
	@Cacheable(value = "cidades", key = "#codigoEstado")	// tem que ter o mesmo nome defino no @RequestParam
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estadoJSON", defaultValue = "-1") Long codigoEstado) {
		// obs.: o Thread abaixo serve apenas para esperar um pouco mais a finalização da pesquisa para podermos
		// mostrar o .gif de processando pesquisa no html. Apenas em desenvolvimento.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {	}
		
		return cidades.findByEstadoCodigo(codigoEstado);
//		return cidades.findAll();
	}
	
//	@CacheEvict(value = "cidades", allEntries=true)
//	@CacheEvict(value = "cidades", key="#cidade.estado.codigo")		// obter o código do Estado a partir do objeto salvo
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()") // a condition valida se o cache deve ser executado
	@Secured(value="ROLE_CADASTRAR_CIDADE")
	@PostMapping(value = {"/nova", "{\\d+}"})
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return nova(cidade);
		}
		
		try {
			cadastroCidadeService.salvar(cidade);
		} catch (NomeCidadeJaCadastradaException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(cidade);
		}
		
		attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		return new ModelAndView("redirect:/cidades/" + cidade.getCodigo());
	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result
			, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");
		mv.addObject("estados", estados.findAll());
		//--- o método cidades.filtrar abaixo está definido em CidadesImpl.java
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable)
				, httpServletRequest);
		//--- o nome 'pagina' é o nome definido na página HTML PesquisaCidades.html
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
}