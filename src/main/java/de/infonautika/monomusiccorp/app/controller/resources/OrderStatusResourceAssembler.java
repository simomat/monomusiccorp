package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.List;

public class OrderStatusResourceAssembler extends ResourceAssemblerSupport<PickingOrder, OrderStatusResource> {
    private Class<?> controllerClass;

    public OrderStatusResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, OrderStatusResource.class);
        this.controllerClass = controllerClass;
    }

    @Override
    public OrderStatusResource toResource(PickingOrder pickingOrder) {
        OrderStatusResource orderStatusResource = new OrderStatusResource();
        orderStatusResource.setSubmitTime(pickingOrder.getOrder().getSubmitTime());
        orderStatusResource.setShippingAddress(pickingOrder.getOrder().getShippingAddress());
        orderStatusResource.setStatus(pickingOrder.getStatus());
        List<PricedPositionResource> pricedPositionResources = new PricedPositionResourceAssembler(controllerClass).toResources(pickingOrder.getOrder().getPositions());

        orderStatusResource.setPositions(pricedPositionResources);
        return orderStatusResource;
    }
}
