package de.infonautika.monomusiccorp.app.business.errors;

public abstract class BusinessError extends RuntimeException {
    public BusinessError(String message) {
        super(message);
    }
}
