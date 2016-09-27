package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;

import java.util.Optional;

public interface CustomerLookup {
    Optional<Customer> getCustomer(String customerId);

    Optional<Customer> getCustomerByName(String userName);

    void save(Customer customer);
}
