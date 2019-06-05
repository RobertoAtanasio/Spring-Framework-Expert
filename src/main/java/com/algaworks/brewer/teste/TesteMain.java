package com.algaworks.brewer.teste;

public class TesteMain {

	public static void main(String[] args) {
		
		String[] romanos = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
		int[] arabicos = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
//		String resultado = "";
		
//		for (int i = 1; i <= 2000; i++) {
//			int numero = i;
//			int indice = ((arabicos.length / arabicos[0])) - 1;
//			while (numero > 0) {
//				if (numero >= arabicos[indice]) {
//					resultado += romanos[indice];
//					numero -= arabicos[indice];
//				} else {
//					indice--;
//				}
//			}
//			System.out.println(i + " = " + resultado);
//			resultado = "";
//		}
		
		// 278
		String[] entrada = {"C","C","L","X","X","V","I","I","I"};
		
		int numero = 0;
		int numero1 = 0;
		int numero2 = 0;
		for (int i = 0; i < entrada.length; i++) {
			int indice = 0;
			for (int j = 0; j < romanos.length; j++) {
				if (entrada[i] == romanos[j]) {
					indice = j;
					if (numero1 == 0) {
						numero1 = arabicos[indice];
					} else {
						if (numero2 == 0) {
							numero2 = arabicos[indice];
							if (numero1 == numero2) {
								numero = numero1 + numero2;
								numero1 = numero2;
								numero2 = 0;
							}
						}
					}
					break;
				}
			}
			
			System.out.println(i + " = " + entrada[i] + " = " + romanos[indice] + " no índice " + indice + " ==> " + numero);
		}
		
		
//		LocalDate dataAux = LocalDate.of(2019, 2, 18);
//		LocalDate dataAtual = LocalDate.now();
//		
//		System.out.println(">>>> Data...: " + dataAux);
//		System.out.println(">>>> Data atual...: " + dataAtual);
//
//		LocalTime hora = LocalTime.of(23, 45, 10);
//		
//		System.out.println(">>>> Hora...: " + hora);
//		System.out.println(">>>> Hora...: " + LocalTime.now());
//		
//		LocalDate parseDate = LocalDate.parse("2019-03-10");
//		LocalTime parseTime = LocalTime.parse("15:25:06");
//		
//		System.out.println(">>>> parseDate...: " + parseDate);
//		System.out.println(">>>> parseTime...: " + parseTime);
//		
//		LocalDateTime ldt = LocalDateTime.now();
//		LocalDateTime ldt2 = LocalDateTime.of(2019,3,10,10,30,2);
//		System.out.println(">>>> LocalDateTime...: " + ldt);
//		System.out.println(">>>> LocalDateTime...: " + ldt2);
//		
//		LocalDateTime ldt3 = LocalDateTime.of(dataAux, hora);
//		System.out.println(">>>> Junção de data e hora...: " + ldt3);
//		
//		LocalDateTime ldt4 = dataAux.atTime(18,51,6);
//		System.out.println(">>>> Junção de data e hora...: " + ldt4);
//		
//		LocalDateTime ldt5 = dataAux.atTime(LocalTime.now());
//		System.out.println(">>>> Junção de data e hora...: " + ldt5);
//	
//		LocalDateTime ldt6 = hora.atDate(dataAux);
//		System.out.println(">>>> Junção de data e hora...: " + ldt6);
		
	}

}
