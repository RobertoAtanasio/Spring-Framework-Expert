package com.algaworks.brewer.repository.helper.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.ClienteResultado;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.ClienteFilter;

public class ClientesImpl implements ClientesQueries {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		
		
		///////////////// TESTES
//		selectUsuarios();
//		selectWhere();
//		parametroPredicate();
//		selectApenasUmCampo();
//		funcoesAgregacaoMedia();
//		resultadoComplexo();
//		resultadoTupla();
//		resultadoClasse();
//		funcaoEqual();
//		funcaoOrdenacao();
		selectJoinFetchCriteria();
//		selectNamedQuery();
//		selectNamedQueryInner();
//		selectUsuariosNamedQueryInner();
//		selectSubQuery();
		/////////////////
		
		
		// Esse método é chamado no ClientesController.java
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
//		System.out.println(">>>> Valor Página Atual: " + paginaAtual);
//		System.out.println(">>>> Valor FisrtResult: " + primeiroRegistro);
//		System.out.println(">>>> Valor MaxResult: " + totalRegistrosPorPagina);

		int teste;
		
		teste = 0;
		
		if (teste == 0) {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		    Root<Cliente> root = cq.from(Cliente.class);
		    cq.orderBy(cb.asc(root.get("nome")));
		    
		    org.springframework.data.domain.Sort sort = pageable.getSort();
		    
		    if (sort != null) {
		    	
		    	org.springframework.data.domain.Sort.Order order = sort.iterator().next();
				String property = order.getProperty();
				
				if (order.isAscending()) {
					cq.orderBy(cb.asc(root.get(property)));
				} else {
					cq.orderBy(cb.desc(root.get(property)));	
				}
		    }
			
		    cq.select(root); 
		    
		    if (!StringUtils.isEmpty(filtro.getNome())) {
		    	cq.where(cb.like(root.get("nome"), "%" + filtro.getNome() + "%"));
		    }
		    
		    if (!StringUtils.isEmpty(filtro.getCpfOuCnpjSemFormatacao())) {
		    	cq.select(root).where(cb.equal(root.get("cpfOuCnpj"), filtro.getCpfOuCnpjSemFormatacao()));
		    }
		    
		    TypedQuery<Cliente> q = em.createQuery(cq)
		    		.setFirstResult(primeiroRegistro)
		    		.setMaxResults(totalRegistrosPorPagina);
		    List<Cliente> lista = q.getResultList();
		    
	//	    List<Cliente> lista = em.createQuery(cq)
	//	    		.setFirstResult(primeiroRegistro)
	//	    		.setMaxResults(totalRegistrosPorPagina)
	//	    		.getResultList();
		    
		    int qtde = em.createQuery(cq).getResultList().size();
		    
		    
		    ////////////////
		    
		    
		    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
	    
		} else {
			
			if (teste == 1) {
	    
				/*
				 * O seguinte SQL:
			    SELECT * FROM prenota p LEFT OUTER JOIN prenota_notafiscal nf
				ON p.codigoprenota = nf.codigoprenota
				where nf.codigoprenota IS NULL

				 * fica formatado da seguinte forma:
 				Query query = _em.createQuery("SELECT p FROM Prenota p LEFT JOIN p.prenotaNotafiscalCollection nf "
                          + " WHERE nf.prenota IS NULL"
                          + " AND "
                          + "(p.dataEmissao BETWEEN :dataInicio AND :dataFim)");
			     */
				
				String sqlString = "SELECT * FROM Cliente c ";
				String sqlAux = "";
				
				if (!StringUtils.isEmpty(filtro.getNome())) {
					sqlAux = "WHERE c.nome LIKE '%" + filtro.getNome() + "%'";
			    }
			    
			    if (!StringUtils.isEmpty(filtro.getCpfOuCnpjSemFormatacao())) {
			    	if (!StringUtils.isEmpty(sqlAux)) {
			    		sqlAux = sqlAux + " AND c.cpf_cnpj = '" + filtro.getCpfOuCnpjSemFormatacao() + "'";
			    	} else {
			    		sqlAux = "WHERE c.cpf_cnpj = '" + filtro.getCpfOuCnpjSemFormatacao() + "'";
			    	}
			    }
			
			    if (!StringUtils.isEmpty(sqlAux)) {
			    	sqlString = sqlString + sqlAux;
			    }
				
			    org.springframework.data.domain.Sort sort = pageable.getSort();
				if (sort != null) {
					org.springframework.data.domain.Sort.Order order = sort.iterator().next();
					String property = order.getProperty();
					sqlString = sqlString + " ORDER BY c." + property + " " + order.getDirection();							
			    } else {
			    	sqlString = sqlString + " ORDER BY c.nome ASC";
			    }
				
				@SuppressWarnings("unchecked")
				TypedQuery<Cliente> query = (TypedQuery<Cliente>) em.createNativeQuery(sqlString, Cliente.class);
				int qtde = query.getResultList().size();
			    query.setFirstResult(primeiroRegistro);
			    query.setMaxResults(totalRegistrosPorPagina);
			    List<Cliente> lista = query.getResultList();
			    
			    return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
			    
			} else {
								
				TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findAllClientes", Cliente.class);
				
				int qtde = query.getResultList().size();
				query.setFirstResult(primeiroRegistro);
				query.setMaxResults(totalRegistrosPorPagina);
							
				List<Cliente> lista = query.getResultList();
						    
				return new PageImpl<> (lista, pageable, Long.valueOf(qtde));
			}
		}
	}
	
	public void selectUsuarios () {
				
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectUsuarios");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
		Root<Usuario> usuario = cq.from(Usuario.class);
		cq.select(usuario);
		cq.where(cb.like(usuario.<String>get("nome"), "%Gabriel%"));
		
		List<Usuario> usuarios = em.createQuery(cq).getResultList();
		
		for (Usuario u : usuarios) {
			System.out.println("Nome: " + u.getNome() + " - " + u.getDataNascimento());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectUsuarios");

	}
	
	

	public void selectWhere () {
		
		//EntityManager em = JPAUtil.createEntityManager();
		
		// select, from, where, like... -> from(), where()..
		// JPQL: from Cliente
		// JPQL: select c from Cliente where c.nome like = 'Fernando%'
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectWhere");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.select(cliente);
		cq.where(cb.like(cliente.<String>get("nome"), "%Pires%"));
		
		List<Cliente> clientes = em.createQuery(cq).getResultList();
		
		for (Cliente clienteLido : clientes) {
			System.out.println("Nome: " + clienteLido.getNome());
			System.out.println("EMail: " + clienteLido.getEmail());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectWhere");

	}
	
	public void parametroPredicate () {
		
		//--- Listar incluindo cláusula WHERE
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE parametroPredicate");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.select(cliente);
		
		List<Predicate> predicates = new ArrayList<>();
		ParameterExpression<String> nomeCliente = cb.parameter(String.class, "nomeCliente");
		predicates.add(cb.like(cliente.get("nome"), nomeCliente));
		cq.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		query.setParameter("nomeCliente", "%Pires%");
				
		List<Cliente> clientes = query.getResultList();
		
		for (Cliente clienteLido: clientes) {
			System.out.println("Nome: " + clienteLido.getNome());
			System.out.println("EMail: " + clienteLido.getEmail());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE parametroPredicate");
		
	}
	
	public void selectApenasUmCampo () {
		
		//--- Projeções
		//--- listar todos, mas retornar apenas um campo 
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectApenasUmCampo");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.select(cliente.<String>get("nome"));
		
		TypedQuery<String> query = em.createQuery(cq);
		
		List<String> nomes = query.getResultList();
		
		for (String nome : nomes) {
			System.out.println("Nome: " + nome);
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectApenasUmCampo");
		
	}
	
	public void funcoesAgregacaoMedia () {
		
		//--- retornar a quantidade de registros lidos
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou média");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		
		Root<Cerveja> cerveja = cq.from(Cerveja.class);
		cq.select(cb.avg(cerveja.<Double>get("valor")));
		
		TypedQuery<Double> query = em.createQuery(cq);
		
		Double media = query.getSingleResult();
		
		System.out.println("Média: " + media);
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou média");
		
	}
	
	public void selectSubQuery () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou selectSubQuery");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cerveja> cq = cb.createQuery(Cerveja.class);
		Subquery<Double> subquery = cq.subquery(Double.class);
		
		Root<Cerveja> cerveja = cq.from(Cerveja.class);
		Root<Cerveja> cervejaSub = subquery.from(Cerveja.class);
		
		subquery.select(cb.avg(cervejaSub.<Double>get("valor")));
		
		Join<Cerveja, Estilo> estilo = (Join) cerveja.fetch("estilo");
		
		cq.select(cerveja);
		cq.where(cb.greaterThanOrEqualTo(cerveja.<Double>get("valor"), subquery));
		
		TypedQuery<Cerveja> tquery = em.createQuery(cq);
		List<Cerveja> resultado = tquery.getResultList();
		
		for (Cerveja c : resultado) {
			System.out.println(c.getSku() + " - " + c.getNome() + " - " + c.getEstilo().getNome() 
					+ " - Valor: " + c.getValor() );
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou selectSubQuery");
	}
	
	public void resultadoComplexo () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE resultadoComplexo");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.multiselect(cliente.get("nome"), cliente.get("cpfOuCnpj"));
		
		TypedQuery<Object[]> query = em.createQuery(cq);
		
		List<Object[]> resultado = query.getResultList();
		
		for (Object[] obj : resultado) {
			System.out.println(obj[0] + " - " + obj[1]);
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE resultadoComplexo");
		
	}
	
	public void resultadoTupla () {
		
		//--- dando nome ao campo retornado
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE resultadoTupla");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.multiselect(cliente.get("nome").alias("nomeCliente"), cliente.get("cpfOuCnpj").alias("CpfCNPJ"));
		
		TypedQuery<Tuple> query = em.createQuery(cq);
		
		List<Tuple> resultado = query.getResultList();
		
		for (Tuple obj : resultado) {
			System.out.println(obj.get("nomeCliente") + " - " + obj.get("CpfCNPJ"));
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE resultadoTupla");
		
	}

	public void resultadoClasse () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE resultadoClasse");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClienteResultado> cq = cb.createQuery(ClienteResultado.class);
		Root<Cliente> cliente = cq.from(Cliente.class);
		cq.select(cb.construct(ClienteResultado.class, cliente.get("nome"), cliente.get("cpfOuCnpj")));
		
		TypedQuery<ClienteResultado> query = em.createQuery(cq);
		
		List<ClienteResultado> resultado = query.getResultList();
		
		for (ClienteResultado obj : resultado) {
			System.out.println(obj.getNome() + " - " + obj.getCpfCnpj());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE resultadoClasse");
		
	}
	
	public void funcaoEqual () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE funcaoEqual");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		
		Root<Cliente> cliente = cq.from(Cliente.class);
		Predicate predicate = cb.equal(cb.upper(cliente.<String>get("tipoPessoa")), TipoPessoa.FISICA);
//		Predicate predicate = cb.equal(cb.upper(cliente.<String>get("nome")), "Katia Roberta Pires".toUpperCase());
		
//		"Katia Roberta Pires".toUpperCase();
		
		cq.select(cliente);
		cq.where(predicate);
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		
		List<Cliente> resultado = query.getResultList();
		
		for (Cliente obj : resultado) {
			System.out.println(obj.getNome() + " - " + obj.getEmail());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE funcaoEqual");
		
	}

	public void funcaoOrdenacao () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE funcaoOrdenacao");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		
		Root<Cliente> cliente = cq.from(Cliente.class);

		Order order = cb.desc(cliente.get("nome"));
				
		cq.select(cliente);
		cq.orderBy(order);
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		
		List<Cliente> resultado = query.getResultList();
		
		for (Cliente obj : resultado) {
			System.out.println(obj.getNome() + " - " + obj.getEmail());
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE funcaoOrdenacao");
		
	}
	
	public void selectJoinFetchCriteria () {
		
		//--- Projeções
		//--- listar todos, mas retornar apenas um campo 
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectJoinFetchCriteria");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
		Root<Usuario> usuario = cq.from(Usuario.class);
		
		Join<Usuario, Grupo> grupo = (Join) usuario.fetch("grupos");
//		Join<Usuario, Grupo> grupo = (Join) usuario.join("grupos");
		
		cq.select(usuario);
		cq.where(cb.equal(grupo.get("codigo"), 2L));	// a clásula where é feita na tabela Grupo
		
		TypedQuery<Usuario> query = em.createQuery(cq);
		
		List<Usuario> lista = query.getResultList();
		
		for (Usuario c : lista) {
			System.out.println(c.getNome() + " - " + c.getEmail() + " - " 
						+ c.getGrupos().get(0).getNome() );
		}
		
		em.close();
		
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectJoinFetchCriteria");
		
	}
	
	public void selectNamedQuery () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectNamedQuery");

		TypedQuery<Cerveja> query = em.createNamedQuery("Cerveja.findAllCervejas", Cerveja.class);
					
		List<Cerveja> lista = query.getResultList();
		
		for (Cerveja c : lista) {
			System.out.println(c.getSku() + " - " + c.getNome() + " - " + c.getEstilo().getNome());
		}
		
		em.close();
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectNamedQuery");
	}

	public void selectNamedQueryInner () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectNamedQueryInner");
		
		TypedQuery<Cerveja> query = em.createNamedQuery("Cerveja.findAllCervejasInner", Cerveja.class);
		
		List<Cerveja> lista = query.getResultList();
		
		for (Cerveja c : lista) {
			System.out.println(c.getSku() + " - " + c.getNome() + " - " + c.getEstilo().getCodigo() + "-" + c.getEstilo().getNome());
		}
		
		em.close();
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectNamedQueryInner");
	}
	
	public void selectUsuariosNamedQueryInner () {
		
		System.out.println(">>>>>>>>>>>>>>>>> Iniciou TESTE selectUsuariosNamedQueryInner");
		
		TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findAllUsuariosInner", Usuario.class);
		
		List<Usuario> lista = query.getResultList();
		
		for (Usuario c : lista) {
			System.out.println(c.getNome() + " - " + c.getEmail() + " - " 
						+ c.getGrupos().get(0).getNome() );
		}
		
		em.close();
		System.out.println(">>>>>>>>>>>>>>>>> Finalizou TESTE selectUsuariosNamedQueryInner");
	}
}
;

/*

------------- EXEMPLO COM MAIS DE UM PARÂMETRO NA CLÁUSULA WHERE -------------

public class AluguelDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public void salvar(Aluguel aluguel) {
		manager.merge(aluguel);
	}

	public List<Aluguel> buscarPorDataDeEntregaEModeloCarro(Date dataEntrega,
			ModeloCarro modelo) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Aluguel> criteriaQuery = builder.createQuery(Aluguel.class);
		Root<Aluguel> a = criteriaQuery.from(Aluguel.class);
		criteriaQuery.select(a);
		
		List<Predicate> predicates = new ArrayList<>();
		if (dataEntrega != null) {
			ParameterExpression<Date> dataEntregaInicial = builder.parameter(Date.class, "dataEntregaInicial");
			ParameterExpression<Date> dataEntregaFinal = builder.parameter(Date.class, "dataEntregaFinal");
			predicates.add(builder.between(a.<Date>get("dataEntrega"), dataEntregaInicial, dataEntregaFinal));
		}
		
		if (modelo != null) {
			ParameterExpression<ModeloCarro> modeloExpression = builder.parameter(ModeloCarro.class, "modelo");
			predicates.add(builder.equal(a.get("carro").get("modelo"), modeloExpression));
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Aluguel> query = manager.createQuery(criteriaQuery);
		
		if (dataEntrega != null) {
			Calendar dataEntregaInicial = Calendar.getInstance();
			dataEntregaInicial.setTime(dataEntrega);
			dataEntregaInicial.set(Calendar.HOUR_OF_DAY, 0);
			dataEntregaInicial.set(Calendar.MINUTE, 0);
			dataEntregaInicial.set(Calendar.SECOND, 0);
			
			Calendar dataEntregaFinal = Calendar.getInstance();
			dataEntregaFinal.setTime(dataEntrega);
			dataEntregaFinal.set(Calendar.HOUR_OF_DAY, 23);
			dataEntregaFinal.set(Calendar.MINUTE, 59);
			dataEntregaFinal.set(Calendar.SECOND, 59);
			
			query.setParameter("dataEntregaInicial", dataEntregaInicial.getTime());
			query.setParameter("dataEntregaFinal", dataEntregaFinal.getTime());
		}
		
		if (modelo != null) {
			query.setParameter("modelo", modelo);
		}
		
		return query.getResultList();
	}

}
*/