package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.ConflictException;
import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import de.infonautika.monomusiccorp.app.domain.Position;

import java.util.List;

public interface BusinessProcess {

    ResultStatus addItemToStock(Quantity<String> quantity);

    ResultStatus putToBasket(String customerId, Quantity<String> quantity);

    List<Position> getBasketContent(String customerId);

    void removeFromBasket(String customerId, Quantity<String> quantity);

    void addCustomer(CustomerInfo customer) throws ConflictException;

    ResultStatus submitOrder(String customerId);

    List<PickingOrder> getPickingOrders(String customerId);
}
