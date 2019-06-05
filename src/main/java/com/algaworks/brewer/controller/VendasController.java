	package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMesDTO;
import com.algaworks.brewer.dto.VendaOrigemDTO;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.service.exception.AccessDeniedException;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {

//	@Autowired
//	private TabelaItensVenda tabelaItensVenda;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Mailer mailer;
	
	@InitBinder("venda")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
	}
	
//	@Autowired
//	private TesteService testeService;
	
//	@GetMapping("/nova")
//	public String nova() {
//		return "venda/CadastroVenda";
//	}
	
//	@GetMapping("/nova")
//	public ModelAndView nova() {
//		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
//		mv.addObject("uuid", UUID.randomUUID().toString());
//		return mv;
//	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		this.setUuid(venda);
		
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);
		
		this.setUuid(venda);
		
		for (ItemVenda item : venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		venda = cadastroVendaService.salvar(venda);		// após a atualização, retorna o objeto atualizado.
		mailer.enviar(venda);
		
		attributes.addFlashAttribute("mensagem", String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMesDTO> listarTotalVendaPorMes() {
		return vendas.totalPorMes();
	}

	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigemDTO> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		
		vendaValidator.validate(venda, result);
	}
	
//	@PostMapping("/item")
//	public @ResponseBody String adicionarItem(Long codigoCerveja) {
//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
//		tabelaItensVenda.adicionarItem(cerveja, 1);
//		System.out.println(">>> total de itens: " + tabelaItensVenda.total());
//		return "Item adicionado!";
//	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
//		tabelaItensVenda.adicionarItem(cerveja, 1);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
//		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
//		mv.addObject("itens", tabelaItensVenda.getItens());
//		System.out.println(">>> total de itens: " + tabelaItensVenda.total());
//		return mv;
		return mvTabelaItensVenda(uuid);
	}
	
//	// o campo codigoCerveja vem no PathVariable e a quantidade vem no corpo do objeto enviado
//	// a quantidade vem do método function onQuantidadeItemAlterado(evento) em venda.autocomplete-itens.js
//	@PutMapping("/item/{codigoCerveja}")
//	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade) {
//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
//		tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);
////		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
////		mv.addObject("itens", tabelaItensVenda.getItens());
////		return mv;
//		return mvTabelaItensVenda();
//	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja
				, Integer quantidade, String uuid) {
//		tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return mvTabelaItensVenda(uuid);
	}
	
//	@DeleteMapping("/item/{codigoCerveja}")
//	public ModelAndView excluirItem(@PathVariable Long codigoCerveja) {
//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
//		tabelaItensVenda.excluirItem(cerveja);
//		return mvTabelaItensVenda();
//	}
	
	// o script abaixo funciona incluindo o método 
	// public DomainClassConverter<FormattingConversionService> domainClassConverter()
	// na classe WebConfig, pois o código cerveja 
	// Obs.: o método alterarQuantidadeItem acima também foi ajustado para tratar desta mesma forma.
	
//	@DeleteMapping("/item/{codigoCerveja}")
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja
				, @PathVariable String uuid ) {
//		tabelaItensVenda.excluirItem(cerveja);
		tabelaItens.excluirItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			System.out.println(">>>>>>>> cancelar >>>>>>");
			return new ModelAndView("/403");
		} 
		
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
//		mv.addObject("itens", tabelaItensVenda.getItens());
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void setUuid(Venda venda) {
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}
	
}