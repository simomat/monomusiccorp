package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.OrderStatusResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import de.infonautika.monomusiccorp.app.repository.PickingOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
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

    @RequestMapping(method = RequestMethod.GET)
    @Secured({CUSTOMER})
    public Resources<OrderStatusResource> getOrders() {
        return currentCustomerProvider.getCustomer()
            .map(customer -> {
                List<PickingOrder> pickingOrders = pickingOrderRepository.findByOrderCustomerId(customer.getId());
                List<OrderStatusResource> orderStatusResources = new OrderStatusResourceAssembler(getClass()).toResources(pickingOrders);
                orderStatusResources.forEach(this::addProductLinks);

                Resources<OrderStatusResource> resources = new Resources<>(orderStatusResources);
                resources.add(linkOn(methodOn(getClass()).getOrders()).withRelSelf());
                return resources;
            })
            .orElseThrow(() -> new ForbiddenException("not a customer"));
    }

    private void addProductLinks(OrderStatusResource orderStatusResource) {
        orderStatusResource.getPositions().forEach(pricedPositionResource ->
            authorizedInvocationFilter.withRightsOn(
                methodOn(CatalogController.class).getProduct(pricedPositionResource.getProductId()),
                invocation -> pricedPositionResource.add(linkOn(invocation).withRel("product")))
        );
    }

}
