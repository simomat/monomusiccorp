package de.infonautika.monomusiccorp.app.business.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends BusinessError {
    public ConflictException(String message) {
        super(message);
    }
}
