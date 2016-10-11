package de.infonautika.monomusiccorp.app.domain;

public class ConflictException extends Exception {
    public ConflictException(String message) {
        super(message);
    }
}
