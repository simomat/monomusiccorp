package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.ForbiddenException;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResource;
import de.infonautika.monomusiccorp.app.controller.resources.PositionResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.Position;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.invocationOf;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.links.InvocationProxy.methodOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkCreator.createLink;

@RestController
@RequestMapping("/api/basket")
public class ShoppingController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CurrentCustomerProvider currentCustomerProvider;

    @RequestMapping(value = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity putToBasket(@PathVariable("productId") String productId, @RequestParam("quantity") Long quantity) {
        return withCustomerId(
            consumerId -> {
                businessProcess.putToBasket(consumerId, productId, quantity);
                return noContent();
            });
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity removeFromBasket(@PathVariable("productId") String productId, @RequestParam("quantity") Long quantity) {
        return withCustomerId(id -> {
            businessProcess.removeFromBasket(id, productId, quantity);
            return noContent();
        });
    }

    @RequestMapping(method = RequestMethod.GET)
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

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public ResponseEntity submitOrder() {
        return withCustomerId(id -> {
            businessProcess.submitOrder(id);
            return noContent();
        });
    }

    private <T> T withCustomerId(Function<String, T> function) {
        return currentCustomerProvider.getCustomerId()
                .map(function)
                .orElseThrow(() -> new ForbiddenException("invalid customer"));
    }

}
