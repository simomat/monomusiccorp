package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CustomerLookup {
    Optional<Customer> getCustomer(String customerId);

    Optional<Customer> getCustomerByName(String userName);

    void save(Customer customer);

    <T> T withCustomer(String customerId, Function<Customer, T> customerMapper, Supplier<T> elseGet);

    void tryWithCustomer(String customerId, Consumer<Customer> consumer);

    List<Customer> findAll();
}

