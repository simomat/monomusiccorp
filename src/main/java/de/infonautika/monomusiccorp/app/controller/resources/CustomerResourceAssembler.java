package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.Customer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class CustomerResourceAssembler extends ResourceAssemblerSupport<Customer, CustomerResource> {
    public CustomerResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, CustomerResource.class);
    }

    @Override
    public CustomerResource toResource(Customer customer) {
        CustomerResource customerResource = new CustomerResource();
        customerResource.setCustomerId(customer.getId());
        customerResource.setUsername(customer.getUsername());
        customerResource.setAddress(customer.getAddress());
        return customerResource;
    }
}
