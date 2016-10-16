package de.infonautika.monomusiccorp.app.intermediate;

import de.infonautika.monomusiccorp.app.domain.Customer;

import java.util.Optional;

public interface CurrentCustomerProvider {
    Optional<Customer> getCustomer();
}
