package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.PricedPosition;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class PricedPositionResourceAssembler extends ResourceAssemblerSupport<PricedPosition, PricedPositionResource> {
    private Class<?> controllerClass;

    public PricedPositionResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, PricedPositionResource.class);
        this.controllerClass = controllerClass;
    }

    @Override
    public PricedPositionResource toResource(PricedPosition pricedPosition) {
        PricedPositionResource pricedPositionResource = new PricedPositionResource();
        pricedPositionResource.setProductId(pricedPosition.getProduct().getId());
        pricedPositionResource.setQuantity(pricedPosition.getQuantity());
        pricedPositionResource.setPrice(pricedPosition.getPrice());
        return pricedPositionResource;
    }
}
