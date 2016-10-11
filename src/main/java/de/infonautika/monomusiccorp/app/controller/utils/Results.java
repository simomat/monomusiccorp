package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class Results {
    public static <T> Supplier<ResponseEntity<T>> notFound() {
        return () -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static <T> Supplier<ResponseEntity<T>> forbidden() {
        return () -> new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<String> conflict(String message) {
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
