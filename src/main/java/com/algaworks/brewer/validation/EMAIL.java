package com.algaworks.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "^\\w*(\\.\\w*)?(\\.[a-z]+)?@\\w*\\.[a-z]+(\\.[a-z]+)?(\\.[a-z]+)?$")
public @interface EMAIL {
	
	/*

public static boolean validar(String email)
    {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

	 */
	
	
	/*
	Expressão: ^\w*(\.\w*)?(\.[a-z]+)?@\w*\.[a-z]+(\.[a-z]+)?(\.[a-z]+)?$
	
	Explicando:

	1 -	A expressão ^ indica o começo da string/linha.
	2 -	\w* pega qualquer caracteres alpha numericos, é o equivalente a [a-zA-Z0-9_]. O asterísco é quantitativo, detectando qualquer quantidade desses caracteres, iniciando no 0 e indo até o infinito.
	3 -	A expressão (\.\w*)? significa: parenteses inicia um agrupamento. A expressão \. detecta literamente um ponto .. A expressão \w* qualquer quantidade de caracteres alpha numéricos.
	4 - O ponto de interrogação (?) é quantitativo: determina que o que vier imediatamente antes dele aparecer na expressão 0 ou 1 vez. Nessa expressão ele aparece duas vezes.
	5 -	O arroba seria o arroba do email mesmo…
	6 -	\w* que aparece depois do arroba já falamos várias vezes logo acima.
	7 -	\.[a-z] pega um ponto seguido de letras minúsculas. vai detectar algo como .com, .net, etc…
	8 -	+ significa que o que estiver imediatamente antes dele precisa aparecer 1 ou mais vezes no termo.
	9 -	(\.[a-z]+): abrimos novamente um agrupamento com o parenteses. \. pega o ponto. A classe [a-z] seleciona qualquer letra minúscula. E o mais aparece novamente, dizendo que tudo aquilo que estiver antes dele deve aparecer pelo menos 1 vez
	10-	E a expressão $ pra finalizar significa final da string.
	 */
	
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "EMail fora de padrão. Verifique o e-mail informado.";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
//	String value() default "";
	
}