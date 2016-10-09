package de.infonautika.monomusiccorp.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class Results {
    public static <T> Supplier<ResponseEntity<T>> notFound() {
        return () -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
