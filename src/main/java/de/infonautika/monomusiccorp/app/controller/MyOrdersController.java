package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.OrderStatusResource;
import de.infonautika.monomusiccorp.app.controller.resources.OrderStatusResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.LinkSupport;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import de.infonautika.monomusiccorp.app.repository.PickingOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
import static de.infonautika.monomusiccorp.app.security.UserRole.ADMIN;
import static de.infonautika.monomusiccorp.app.security.UserRole.CUSTOMER;

@RestController
@RequestMapping("/api/orders")
public class MyOrdersController implements SelfLinkSupplier {

    @Autowired
    private PickingOrderRepository pickingOrderRepository;

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

    @Autowired
    private CurrentCustomerProvider currentCustomerProvider;

    @Autowired
    private CustomerLookup customerLookup;

    @RequestMapping(method = RequestMethod.GET)
    @Secured({CUSTOMER})
    public Resources<OrderStatusResource> getOrders() {
        return currentCustomerProvider.getCustomer()
            .map(this::getOrderStatusResource)
            .map(addLink(methodOn(getClass()).getOrders(), Link.REL_SELF))
            .orElseThrow(() -> new ForbiddenException("not a customer"));
    }

    @RequestMapping(value = "{customer}", method = RequestMethod.GET)
    @Secured({ADMIN})
    public Resources<OrderStatusResource> getOrders(@PathVariable("customer") String customerDescriptor) {
        return getCustomer(customerDescriptor)
                .map(this::getOrderStatusResource)
                .map(addLink(methodOn(getClass()).getOrders(customerDescriptor), "customerorders"))
                .orElseThrow(() -> new DoesNotExistException("no customer of '" + customerDescriptor + "' found"));
    }

    private Optional<Customer> getCustomer(@PathVariable("customer") String customerDescriptor) {
        Optional<Customer> customer = customerLookup.getCustomerByName(customerDescriptor);
        if (customer.isPresent()) {
            return customer;
        }
        return customerLookup.getCustomer(customerDescriptor);
    }

    private Resources<OrderStatusResource> getOrderStatusResource(Customer customer) {
        List<PickingOrder> pickingOrders = pickingOrderRepository.findByOrderCustomerId(customer.getId());
        List<OrderStatusResource> orderStatusResources = new OrderStatusResourceAssembler(getClass()).toResources(pickingOrders);
        orderStatusResources.forEach(this::addProductLinks);
        return new Resources<>(orderStatusResources);
    }


    private void addProductLinks(OrderStatusResource orderStatusResource) {
        orderStatusResource.getPositions().forEach(pricedPositionResource ->
            authorizedInvocationFilter.withRightsOn(
                methodOn(CatalogController.class).getProduct(pricedPositionResource.getProductId()),
                invocation -> pricedPositionResource.add(linkOn(invocation).withRel("product")))
        );
    }

    private Function<Resources<OrderStatusResource>, Resources<OrderStatusResource>> addLink(Object invocation, String relationName) {
        return orderStatusResources -> {
            authorizedInvocationFilter.withRightsOn(
                    invocation,
                    LinkSupport.addLink(orderStatusResources, relationName));
            return orderStatusResources;
        };
    }

}
