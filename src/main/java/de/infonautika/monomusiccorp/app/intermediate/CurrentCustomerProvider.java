package de.infonautika.monomusiccorp.app.intermediate;

import java.util.Optional;

public interface CurrentCustomerProvider {
    Optional<String> getCustomerId();
}
