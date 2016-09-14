package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.ShoppingBasket;

import java.util.Optional;

public interface CustomerLookup {
    Optional<ShoppingBasket> getShoppingBasketOfCustomer(String customerId);

    Optional<Customer> getCustomer(String customerId);

    Optional<Customer> getCustomerByName(String userName);
}
