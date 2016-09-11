package de.infonautika.monomusiccorp.app.intermediate;

import java.util.Optional;

public interface CustomerProvider {
    Optional<String> getCustomerId();
}
