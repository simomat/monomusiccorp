package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.CustomerInfo;
import de.infonautika.monomusiccorp.app.controller.resources.CustomerResource;
import de.infonautika.monomusiccorp.app.controller.resources.CustomerResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.addLink;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
import static de.infonautika.monomusiccorp.app.security.UserRole.ADMIN;

@RestController
@RequestMapping("/api/customer")
public class CustomerController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CustomerLookup customerLookup;

    @Autowired
    private CurrentCustomerProvider currentCustomerProvider;

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

    @RequestMapping(method = RequestMethod.GET)
    @Relation("customer")
    public ResponseEntity getCurrent() {

        Optional<Customer> customer = currentCustomerProvider.getCustomer();
        ResourceSupport resource;
        if (customer.isPresent()) {
            resource = toCustomerResource(customer.get());
        } else {
            resource = new ResourceSupport();
        }

        if (!hasSelfLink(resource)) {
            addSelfLink(resource);
        }

        authorizedInvocationFilter.withRightsOn(
                methodOn(getClass()).getCustomers(),
                addLink(resource)
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(getClass()).register(null),
                addLink(resource)
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(getClass()).getCustomer(null),
                addLink(resource)
        );

        return ResponseEntity.ok(resource);
    }

    private boolean hasSelfLink(ResourceSupport resource) {
        return resource.getLinks().stream()
                .anyMatch(link -> link.getRel().equals(Link.REL_SELF));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Secured({ADMIN})
    public Resources<CustomerResource> getCustomers() {
        List<CustomerResource> customers = new CustomerResourceAssembler(getClass()).toResources(customerLookup.findAll());
        customers.forEach(this::addCustomerLink);

        Resources<CustomerResource> customerResources = new Resources<>(customers);
        customerResources.add(linkOn(methodOn(getClass()).getCustomers()).withRelSelf());

        return customerResources;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CustomerInfo customer) {
        businessProcess.addCustomer(customer);
        return noContent();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    @Secured({ADMIN})
    public HttpEntity<CustomerResource> getCustomer(@PathVariable("userName") String userName) {
        return customerLookup.getCustomerByName(userName)
                .map(this::toCustomerResource)
                .map(ResponseEntity::ok)
                .orElseGet(notFound());
    }

    private CustomerResource toCustomerResource(Customer customer) {
        CustomerResource customerResource = new CustomerResourceAssembler(getClass()).toResource(customer);
        customerResource.add(linkOn(methodOn(getClass()).getCustomer(customer.getUsername())).withRelSelf());
        return customerResource;
    }

    private void addCustomerLink(CustomerResource customerResource) {
        customerResource.add(linkOn(methodOn(getClass()).getCustomer(customerResource.getUsername())).withRelSelf());
    }


}
