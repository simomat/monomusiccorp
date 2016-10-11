package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class CustomerLookupImpl implements CustomerLookup {

    Logger logger = LoggerFactory.getLogger(CustomerLookupImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<Customer> getCustomer(String customerId) {
        return Optional.of(customerRepository.findById(customerId));
    }

    @Override
    public Optional<Customer> getCustomerByName(String userName) {
        return Optional.ofNullable(customerRepository.findByUsername(userName));
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public  <T> T withCustomer(String customerId, Function<Customer, T> customerMapper, Supplier<T> elseGet) {
        return getCustomer(customerId)
                .map(customerMapper)
                .orElseGet(() -> {
                    logger.debug("no customer with id {} found", customerId);
                    return elseGet.get();
                });
    }

    @Override
    public void tryWithCustomer(String customerId, Consumer<Customer> consumer) {
        getCustomer(customerId).ifPresent(consumer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
