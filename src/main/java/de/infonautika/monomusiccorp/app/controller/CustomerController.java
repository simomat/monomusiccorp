package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.CustomerInfo;
import de.infonautika.monomusiccorp.app.controller.resources.CustomerResource;
import de.infonautika.monomusiccorp.app.controller.resources.CustomerResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.Invocation;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.ConflictException;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.*;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.*;
import static de.infonautika.monomusiccorp.app.security.UserRole.ADMIN;

@RestController
@RequestMapping("/api/customer")
public class CustomerController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CustomerLookup customerLookup;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping(method = RequestMethod.GET)
    @Secured(ADMIN)
    public Resources<CustomerResource> getCustomers(){
        List<CustomerResource> customers = new CustomerResourceAssembler(getClass()).toResources(customerLookup.findAll());
        customers.forEach(this::addCustomerLink);

        Resources<CustomerResource> customerResources = new Resources<>(customers);
        addSelfLink(customerResources);

        return customerResources;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CustomerInfo customer){
        try {
            businessProcess.addCustomer(customer);
        } catch (ConflictException e) {
            return conflict(e.getMessage());
        }
        return noContent();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    public HttpEntity<CustomerResource> getCustomer(@PathVariable String userName) {
        return authenticationFacade.getCurrentUserName()
                .filter(name -> name.equals(userName))
                .map(name -> customerLookup.getCustomer(userName)
                        .map(this::toCustomerResource)
                        .map(ResponseEntity::ok)
                        .orElseGet(notFound()))
                .orElseGet(forbidden());
    }

    private CustomerResource toCustomerResource(Customer customer) {
        CustomerResource customerResource = new CustomerResourceAssembler(getClass()).toResource(customer);
        customerResource.add(createSelfLink(Invocation.invocationOf(Invocation.methodOn(getClass()).getCustomer(customer.getUsername()))));
        return customerResource;
    }

    private void addCustomerLink(CustomerResource customerResource) {
        customerResource.add(createSelfLink(Invocation.invocationOf(Invocation.methodOn(getClass()).getCustomer(customerResource.getUsername()))));
    }


}
