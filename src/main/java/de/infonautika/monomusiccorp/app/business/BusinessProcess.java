package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;

import java.util.Collection;
import java.util.List;

public interface BusinessProcess {
    Collection<Product> getAllProducts();

    ResultStatus addItemToStock(Quantity<ItemId> quantity);

    Collection<StockItem> getStocks();

    ResultStatus putToBasket(String customerId, Quantity<ItemId> quantity);

    List<Quantity<Product>> getBasketContent(String customerId);

    void removeFromBasket(String customerId, Quantity<ItemId> quantity);

    ResultStatus addCustomer(CustomerInfo customer);

    void submitOrder(String customerId);
}
