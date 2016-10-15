package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.Position;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class PositionResourceAssembler extends ResourceAssemblerSupport<Position, PositionResource> {
    public PositionResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, PositionResource.class);
    }

    @Override
    public PositionResource toResource(Position position) {
        PositionResource positionResource = new PositionResource();
        positionResource.setProductId(position.getProduct().getId());
        positionResource.setQuantity(position.getQuantity());
        return positionResource;
    }
}
