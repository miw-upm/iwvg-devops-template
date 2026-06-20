package es.upm.api.services.exceptions;

public class ClientBusinessException extends RuntimeException {
    public ClientBusinessException(String userMessage) {
        super(userMessage);
    }
}



