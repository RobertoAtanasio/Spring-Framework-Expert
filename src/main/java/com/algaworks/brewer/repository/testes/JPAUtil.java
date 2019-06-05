package com.algaworks.brewer.repository.testes;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jdbc/brewerDB");
	
	public static EntityManager createEntityManager() {
		return emf.createEntityManager();
	}
}
