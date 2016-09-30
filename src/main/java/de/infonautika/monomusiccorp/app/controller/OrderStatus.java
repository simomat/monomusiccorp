package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public class OrderStatus {
    private LocalDateTime submitTime;
    private List<PricedPosition> positions;
    private Address shippingAddress;
    private PickingOrder.PickingStatus status;

    public static OrderStatus from(PickingOrder pickingOrder) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.submitTime = pickingOrder.getOrder().getSubmitTime();
        orderStatus.positions = pickingOrder.getOrder().getPositions();
        orderStatus.shippingAddress = pickingOrder.getOrder().getShippingAddress();
        orderStatus.status = pickingOrder.getStatus();
        return orderStatus;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public List<PricedPosition> getPositions() {
        return positions;
    }

    public PickingOrder.PickingStatus getStatus() {
        return status;
    }
}
