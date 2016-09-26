package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.domain.Address;
import de.infonautika.monomusiccorp.app.domain.Order;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import de.infonautika.monomusiccorp.app.domain.Position;

import java.time.LocalDateTime;
import java.util.List;

public class OrderStatus {
    private LocalDateTime submitTime;
    private List<Position> positions;
    private Address shippingAddress;
    private PickingOrder.PickingStatus status;

    public static OrderStatus from(Order order) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.submitTime = order.getSubmitTime();
        orderStatus.positions = order.getPositions();
        orderStatus.shippingAddress = order.getShippingAddress();
        orderStatus.status = order.getPickingOrder().getStatus();
        return orderStatus;
    }
}
