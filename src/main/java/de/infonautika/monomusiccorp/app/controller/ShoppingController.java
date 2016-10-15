package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.OrderStatusResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResource;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import de.infonautika.monomusiccorp.app.domain.Position;
import de.infonautika.monomusiccorp.app.intermediate.CustomerProvider;
import de.infonautika.monomusiccorp.app.repository.PickingOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.invocationOf;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.*;
import static de.infonautika.monomusiccorp.app.controller.utils.links.InvocationProxy.methodOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkCreator.createLink;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

    @Autowired
    private PickingOrderRepository pickingOrderRepository;

    @RequestMapping(value = "/basket/{productId}", method = RequestMethod.POST)
    public ResponseEntity putToBasket(@PathVariable("productId") String productId, @RequestParam("quantity") Long quantity) {
        return withCustomerId(
            consumerId -> {
                businessProcess.putToBasket(consumerId, productId, quantity);
                return noContent();
            });
    }

    @RequestMapping(value = "/basket/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity removeFromBasket(@PathVariable("productId") String productId, @RequestParam("quantity") Long quantity) {
        return withCustomerId(id -> {
            businessProcess.removeFromBasket(id, productId, quantity);
            return noContent();
        });
    }

    @RequestMapping(value = "/basket", method = RequestMethod.GET)
    public Resources<PositionResource> getBasket() {
        return withCustomerId(
                id -> {
                    List<Position> basketContent = businessProcess.getBasketContent(id);
                    List<PositionResource> positionResources = new PositionResourceAssembler(getClass()).toResources(basketContent);
                    positionResources.forEach(positionResource ->
                            positionResource.add(
                                createLink(invocationOf(methodOn(CatalogController.class).getProduct(positionResource.getProductId())))
                                        .withRel("product")));

                    Resources<PositionResource> resources = new Resources<>(positionResources);
                    resources.add(createLink(invocationOf(methodOn(getClass()).getBasket())).withRelSelf());
                    return resources;
                });
    }

    @RequestMapping(value = "/submitorder", method = RequestMethod.GET)
    public ResponseEntity submitOrder() {
        return withCustomerId(id -> {
            businessProcess.submitOrder(id);
            return noContent();
        });
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Resources<OrderStatusResource> getOrders() {
        return withCustomerId(
            id -> {
                List<PickingOrder> pickingOrders = pickingOrderRepository.findByOrderCustomerId(id);
                List<OrderStatusResource> orderStatusResources = new OrderStatusResourceAssembler(getClass()).toResources(pickingOrders);
                orderStatusResources.forEach(this::addProductLinks);

                Resources<OrderStatusResource> resources = new Resources<>(orderStatusResources);
                resources.add(createLink(invocationOf(methodOn(getClass()).getOrders())).withRelSelf());
                return resources;
            });
}

    private void addProductLinks(OrderStatusResource orderStatusResource) {
        orderStatusResource.getPositions().forEach(pricedPositionResource ->
            authorizedInvocationFilter.withRightsOn(
                invocationOf(methodOn(CatalogController.class).getProduct(pricedPositionResource.getProductId())),
                invocation -> pricedPositionResource.add(createLink(invocation).withRel("product")))
        );
    }

    private <T> T withCustomerId(Function<String, T> function) {
        return customerProvider.getCustomerId()
                .map(function)
                .orElseThrow(() -> new ForbiddenException("invalid customer"));
    }

}
