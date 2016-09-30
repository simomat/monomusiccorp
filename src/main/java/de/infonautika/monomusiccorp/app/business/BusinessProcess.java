package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.*;

import java.util.Collection;
import java.util.List;

public interface BusinessProcess {
    Collection<Product> getAllProducts();

    ResultStatus addItemToStock(Quantity<ItemId> quantity);

    Collection<StockItem> getStocks();

    ResultStatus putToBasket(String customerId, Quantity<ItemId> quantity);

    List<PricedPosition> getBasketContent(String customerId);

    void removeFromBasket(String customerId, Quantity<ItemId> quantity);

    ResultStatus addCustomer(CustomerInfo customer);

    ResultStatus submitOrder(String customerId);

    List<Order> getOrders(String customerId);

    List<PickingOrder> getPickingOrders(String customerId);
}
