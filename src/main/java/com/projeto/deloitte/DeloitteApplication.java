	package com.projeto.deloitte;

	import io.swagger.v3.oas.annotations.OpenAPIDefinition;
	import io.swagger.v3.oas.annotations.info.Info;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;

	@SpringBootApplication
	@OpenAPIDefinition(info = @Info(title = "API de Agendamento Deloitte", version = "1.0", description = "Documentação da API do Sistema Simplificado de Agendamento de Consultas/Serviços"))
	public class DeloitteApplication {

		public static void main(String[] args) {
			SpringApplication.run(DeloitteApplication.class, args);
		}

	}
