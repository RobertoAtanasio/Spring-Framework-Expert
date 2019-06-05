 package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cervejas")
public class CervejasController {
	
	//--- @Autowired significa injeção de dependência para este objeto
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;

	@Autowired
	private Cervejas cervejas;
	
	@RequestMapping("/nova")
	public ModelAndView nova(Cerveja cerveja) {
		
		ModelAndView mv = new ModelAndView("cerveja/CadastroCerveja");
		mv.addObject("sabores", Sabor.values());
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("origens", Origem.values());
		return mv;
	}
	
	// a expressão regular "{\\d+}" diz que qualquer dígito inteiro e para salvar como parâmetro
//	@RequestMapping(value = "/nova", method = RequestMethod.POST)
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cerveja cerveja, BindingResult result, Model model, RedirectAttributes attributes) {
		
		if (result.hasErrors()) {
			return nova(cerveja);
		}
		cadastroCervejaService.salvar(cerveja);
		attributes.addFlashAttribute("mensagem", "Cerveja salva com sucesso!");
//		System.out.println("*** Cerveja salva com sucesso! ***");
		return new ModelAndView("redirect:/cervejas/nova");
	}
	
	// O @PageableDefault tem um tamanho de 20, logo, se não informar esse parâmetro, assume 20.
	
	// O nome 'pagina' definido na tag mv.addObject("pagina", paginaWrapper) é o nome de referência que será utilizado
	// no html da página de pesquisa
	
	@GetMapping
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult binding
			, @PageableDefault(size=2) Pageable pageable,  HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cerveja/PesquisaCervejas");
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());
		
//		System.out.println(">>>>> pageNumber: " + pageable.getPageNumber() );
//		System.out.println(">>>>> pageSize: " + pageable.getPageSize() );
		
//		mv.addObject("cervejas", cervejas.findAll(pageable));
//		mv.addObject("cervejas", cervejas.filtrar(cervejaFilter, pageable));
//		Page<Cerveja> pagina = cervejas.filtrar(cervejaFilter, pageable);
		PageWrapper<Cerveja> paginaWrapper = new PageWrapper<>(cervejas.filtrar(cervejaFilter, pageable)
				, httpServletRequest);
//		mv.addObject("pagina", pagina);
		mv.addObject("pagina", paginaWrapper);
		
//		System.out.println(">>>> Número da página (number): " + pagina.getNumber());
//		System.out.println(">>>> Total de páginas (totalPages): " + pagina.getTotalPages());
		
		return mv;
	}
	
//	@GetMapping("/filtro")
//	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
//		return cervejas.porSkuOuNome(skuOuNome);
//	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cadastroCervejaService.excluir(cerveja);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage()); 
			// esta mensagem vai para o error: onErroExcluir.bind(this) do javascript
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja) {
		
		ModelAndView mv = this.nova(cerveja);
		mv.addObject(cerveja);
		return mv;
	}
}