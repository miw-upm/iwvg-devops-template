package es.upm.api.services.exceptions;

public class InfrastructureException extends ApiException {
    private static final String DESCRIPTION = "Infrastructure Exception";

    public InfrastructureException(String detail) {
        super(DESCRIPTION, detail);
    }

    public InfrastructureException(String detail, Throwable cause) {
        super(DESCRIPTION, detail, cause);
    }
}
