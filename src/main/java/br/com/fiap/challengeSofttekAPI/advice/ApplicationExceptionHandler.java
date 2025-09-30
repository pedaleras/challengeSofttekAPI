package br.com.fiap.challengeSofttekAPI.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    // Construtor adicionado para logar a inicialização do Advice
    public ApplicationExceptionHandler() {
        log.info("ApplicationExceptionHandler inicializado. Pronto para tratar exceções.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> manusearArgumentosInvalidos(MethodArgumentNotValidException ex){ // Renomeado o parâmetro para 'ex' para clareza
        Map<String,String> mapaErro = new HashMap<>();
        List<FieldError> campos = ex.getBindingResult().getFieldErrors();

        // Log de WARN para erros de validação, pois são geralmente causados por entrada inválida do usuário.
        // Inclui os detalhes dos campos com erro para facilitar a depuração.
        log.warn("Erro de validação de argumento recebido: {}", ex.getMessage());
        for(FieldError campo: campos){
            mapaErro.put(campo.getField(),campo.getDefaultMessage());
            log.warn("Campo: '{}', Erro: '{}'", campo.getField(), campo.getDefaultMessage());
        }

        return mapaErro;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> manusearIntegridadeDados(DataIntegrityViolationException ex){ // Adicionado o parâmetro 'ex'
        Map<String,String> mapaErro = new HashMap<>();
        mapaErro.put("erro","Usuário já cadastrado!");
        // Log de ERROR para violação de integridade de dados, pois geralmente é um problema que precisa de atenção.
        // É importante passar a exceção 'ex' como último argumento para que o stack trace completo seja logado.
        log.error("Erro de violação de integridade de dados (Ex: usuário já cadastrado): {}", ex.getMessage(), ex);
        return mapaErro;
    }

    // Handler genérico para capturar qualquer outra exceção não tratada especificamente.
    // Isso é CRÍTICO para garantir que nenhum erro no backend passe despercebido.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Retorna 500 para erros internos não esperados
    public ResponseEntity<Map<String, String>> handleAllUncaughtException(Exception ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("erro", "Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde.");

        // Log de ERROR para todas as exceções não tratadas.
        // É fundamental passar a exceção 'ex' como último argumento para que o stack trace completo seja logado.
        log.error("Ocorreu um erro interno inesperado e não capturado por um handler específico: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}