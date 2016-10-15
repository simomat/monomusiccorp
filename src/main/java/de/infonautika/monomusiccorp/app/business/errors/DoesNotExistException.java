package de.infonautika.monomusiccorp.app.business.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DoesNotExistException extends BusinessError {
    public DoesNotExistException(String message) {
        super(message);
    }
}
