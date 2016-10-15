package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.Position;

import java.util.List;

public interface BusinessProcess {

    ResultStatus addItemToStock(Quantity<String> quantity);

    void putToBasket(String customerId, String productId, Long quantity);

    List<Position> getBasketContent(String customerId);

    void removeFromBasket(String customerId, String productId, Long quantity);

    void addCustomer(CustomerInfo customer);

    void submitOrder(String customerId);
}
