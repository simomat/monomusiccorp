package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.repository.CustomerRepository;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerProviderImpl implements CustomerProvider {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<String> getCustomerId() {
        return authenticationFacade.getCurrentUserName()
                .flatMap(this::toCustomerId);
    }

    private Optional<String> toCustomerId(String userName) {
        Customer customer = customerRepository.findByUsername(userName);
        if (customer == null) {
            return Optional.empty();
        }
        return Optional.of(customer.getId());
    }
}
