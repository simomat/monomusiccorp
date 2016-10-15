package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.Position;
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

        // TODO: 14.10.16 make PricedPosition extend Position and PricedPositionResourceAssembler extend PositionResourceAssembler
        Position position = new Position(pricedPosition.getProduct(), pricedPosition.getQuantity());
        PositionResource positionResource = new PositionResourceAssembler(controllerClass).toResource(position);

        PricedPositionResource pricedPositionResource = new PricedPositionResource();
        pricedPositionResource.setProductId(positionResource.getProductId());
        pricedPositionResource.setQuantity(positionResource.getQuantity());
        pricedPositionResource.setPrice(pricedPosition.getPrice());
        return pricedPositionResource;
    }
}
