package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerLookupImpl implements CustomerLookup {

    Logger logger = LoggerFactory.getLogger(CustomerLookupImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<Customer> getCustomerById(String customerId) {
        return Optional.ofNullable(customerRepository.findById(customerId));
    }

    @Override
    public Optional<Customer> getCustomerByName(String userName) {
        return Optional.ofNullable(customerRepository.findByUserUsername(userName));
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
