package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.*;

import java.util.Collection;
import java.util.List;

public interface BusinessProcess {
    Collection<Product> getAllProducts();

    ResultStatus addItemToStock(Quantity<String> quantity);

    Collection<StockItem> getStocks();

    ResultStatus putToBasket(String customerId, Quantity<String> quantity);

    List<Position> getBasketContent(String customerId);

    void removeFromBasket(String customerId, Quantity<String> quantity);

    ResultStatus addCustomer(CustomerInfo customer);

    ResultStatus submitOrder(String customerId);

    List<PickingOrder> getPickingOrders(String customerId);
}
