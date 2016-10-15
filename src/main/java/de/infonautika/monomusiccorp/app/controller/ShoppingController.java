package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResource;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.Position;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;

@RestController
@RequestMapping("/api/basket")
public class ShoppingController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CurrentCustomerProvider currentCustomerProvider;

    @RequestMapping(value = "/{productId}", method = RequestMethod.POST)
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
    public ResponseEntity removeFromBasket(
            @PathVariable("productId") String productId,
            @RequestParam(value = "quantity", required = false, defaultValue="1") Long quantity) {
                return withCustomer(customer -> {
                    businessProcess.removeFromBasket(customer, productId, quantity);
                    return noContent();
                });
    }

    @RequestMapping(method = RequestMethod.GET)
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
        resources.add(linkOn(methodOn(getClass()).putToBasket(null, null)).withRel("add"));
        resources.add(linkOn(methodOn(getClass()).removeFromBasket(null, null)).withRel("remove"));
    }

    private void addProductLinks(List<PositionResource> positionResources) {
        positionResources.forEach(positionResource -> {
            positionResource.add(
                    linkOn((methodOn(CatalogController.class).getProduct(positionResource.getProductId())))
                            .withRel("product"));
        });
    }

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
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
