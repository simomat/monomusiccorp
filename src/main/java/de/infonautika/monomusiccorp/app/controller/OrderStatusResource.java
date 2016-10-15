package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.PricedPositionResource;
import de.infonautika.monomusiccorp.app.domain.Address;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDateTime;
import java.util.List;

public class OrderStatusResource extends ResourceSupport {
    private LocalDateTime submitTime;
    private List<PricedPositionResource> positions;
    private Address shippingAddress;
    private PickingOrder.PickingStatus status;

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public List<PricedPositionResource> getPositions() {
        return positions;
    }

    public PickingOrder.PickingStatus getStatus() {
        return status;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public void setPositions(List<PricedPositionResource> positions) {
        this.positions = positions;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setStatus(PickingOrder.PickingStatus status) {
        this.status = status;
    }
}
