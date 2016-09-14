package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.ShoppingBasket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerLookupImpl implements CustomerLookup {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<ShoppingBasket> getShoppingBasketOfCustomer(String customerId) {
        return getCustomer(customerId)
                .map(Customer::getShoppingBasket);
    }

    @Override
    public Optional<Customer> getCustomer(String customerId) {
        return Optional.of(customerRepository.findById(customerId));
    }

    @Override
    public Optional<Customer> getCustomerByName(String userName) {
        return Optional.ofNullable(customerRepository.findByUsername(userName));
    }
}