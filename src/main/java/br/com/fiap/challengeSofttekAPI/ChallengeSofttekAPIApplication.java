package br.com.fiap.challengeSofttekAPI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j // Anotação @Slf4j adicionada para gerar automaticamente uma instância de logger (chamada 'log')
public class ChallengeSofttekAPIApplication {

	public static void main(String[] args) {
		// Log para indicar o início da execução da aplicação.
		// Nível INFO é adequado para eventos importantes do ciclo de vida da aplicação.
		log.info("Iniciando a aplicação challengeSofttekAPI...");

		SpringApplication.run(ChallengeSofttekAPIApplication.class, args);

		// Log para indicar que a aplicação foi iniciada com sucesso.
		log.info("Aplicação challengeSofttekAPI iniciada e pronta para receber requisições!");
	}

}