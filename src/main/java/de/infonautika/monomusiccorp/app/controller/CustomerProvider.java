package de.infonautika.monomusiccorp.app.controller;

import java.util.Optional;

public interface CustomerProvider {
    Optional<String> getCustomerId();
}
