package com.algaworks.brewer.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeracaoSenhaTeste {

	public static void main(String[] args) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("admin"));
		
//		1. forEach and Map
//		1.1 Normal way to loop a Map.

			Map<String, Integer> items = new HashMap<>();
			items.put("A", 10);
			items.put("B", 20);
			items.put("C", 30);
			items.put("D", 40);
			items.put("E", 50);
			items.put("F", 60);

			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
			}
		
//		1.2 In Java 8, you can loop a Map with forEach + lambda expression.

			Map<String, Integer> items3 = new HashMap<>();
			items3.put("A", 10);
			items3.put("B", 20);
			items3.put("C", 30);
			items3.put("D", 40);
			items3.put("E", 50);
			items3.put("F", 60);
			
			items3.forEach((k,v)->System.out.println("> Item : " + k + " Count : " + v));
			
			items3.forEach((k,v)->{
				System.out.println("< Item : " + k + " Count : " + v);
				if("E".equals(k)){
					System.out.println("Hello E");
				}
			});
		
		// 2. forEach and List
		
		List<String> items2 = new ArrayList<>();
		items2.add("A");
		items2.add("B");
		items2.add("C");
		items2.add("D");
		items2.add("E");

		//lambda
		//Output : A,B,C,D,E
		System.out.println("------------------------------------");
		System.out.println("Output : A,B,C,D,E");
		items2.forEach(item->System.out.println(">> " + item));
			
		//Output : C
		System.out.println("------------------------------------");
		System.out.println("Output : C");
		items2.forEach(item->{
			if("C".equals(item)){
				System.out.println("Igual a " + item);
			}
		});
			
		//method reference
		//Output : A,B,C,D,E
		System.out.println("------------------------------------");
		System.out.println("Output : A,B,C,D,E");
		items2.forEach(System.out::println);
		
		//Stream and filter
		//Output : B
		System.out.println("------------------------------------");
		System.out.println("Output : B");
		items2.stream()
			.filter(s->s.contains("B"))
			.forEach(System.out::println);
		
	}

}
