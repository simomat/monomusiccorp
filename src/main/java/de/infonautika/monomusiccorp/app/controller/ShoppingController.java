package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResource;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.Position;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
import static de.infonautika.monomusiccorp.app.security.UserRole.CUSTOMER;

@RestController
@RequestMapping("/api/basket")
public class ShoppingController {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CurrentCustomerProvider currentCustomerProvider;

    @RequestMapping(value = "/{productId}", method = RequestMethod.POST)
    @Secured(CUSTOMER)
    @Relation("basketput")
    public ResponseEntity putToBasket(
            @PathVariable("productId") String productId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") Long quantity) {
        return withCustomer(
            consumer -> {
                businessProcess.putToBasket(consumer, productId, quantity);
                return noContent();
            });
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    @Secured(CUSTOMER)
    @Relation("basketdel")
    public ResponseEntity removeFromBasket(
            @PathVariable("productId") String productId,
            @RequestParam(value = "quantity", required = false, defaultValue="1") Long quantity) {
                return withCustomer(customer -> {
                    businessProcess.removeFromBasket(customer, productId, quantity);
                    return noContent();
                });
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured(CUSTOMER)
    @Relation("basket")
    public Resources<PositionResource> getBasket() {
        return withCustomer(
                customer -> {
                    List<Position> basketContent = customer.getShoppingBasket().getPositions();
                    List<PositionResource> positionResources = new PositionResourceAssembler(getClass()).toResources(basketContent);
                    addProductLinks(positionResources);

                    Resources<PositionResource> resources = new Resources<>(positionResources);
                    addBasketLinks(resources);

                    return resources;
                });
    }

    private void addBasketLinks(Resources<PositionResource> resources) {
        resources.add(linkOn(methodOn(getClass()).getBasket()).withRelSelf());
        resources.add(linkOn(methodOn(getClass()).putToBasket(null, null)).withGivenRel());
        resources.add(linkOn(methodOn(getClass()).removeFromBasket(null, null)).withGivenRel());
        resources.add(linkOn(methodOn(getClass()).submitOrder()).withGivenRel());
    }

    private void addProductLinks(List<PositionResource> positionResources) {
        positionResources.forEach(positionResource ->
                positionResource.add(
                    linkOn((methodOn(CatalogController.class).getProduct(positionResource.getProductId())))
                            .withGivenRel()));
    }

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    @Secured(CUSTOMER)
    @Relation("submit")
    public ResponseEntity submitOrder() {
        return withCustomer(
                customer -> {
                    businessProcess.submitOrder(customer);
                    return noContent();
                });
    }

    private <T> T withCustomer(Function<Customer, T> function) {
        return currentCustomerProvider.getCustomer()
                .map(function)
                .orElseThrow(() -> new ForbiddenException("invalid customer"));
    }

}
