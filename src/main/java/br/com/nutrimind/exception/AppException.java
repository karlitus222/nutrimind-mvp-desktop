package br.com.nutrimind.exception;

/**
 * Exceção padrão da aplicação Nutrimind.
 *
 * Encapsula erros de negócio e de persistência com mensagens
 * legíveis para exibição ao usuário via interface Swing.
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
