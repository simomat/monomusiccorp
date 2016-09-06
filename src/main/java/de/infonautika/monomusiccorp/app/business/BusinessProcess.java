package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.controller.CustomerInfo;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.util.ResultStatus;

import java.util.Collection;
import java.util.List;

public interface BusinessProcess {
    void createDatabase();

    Collection<Product> getAllProducts();

    void addItemToStock(Quantity<ItemId> quantity);

    Collection<StockItem> getStocks();

    void putToBasket(Quantity<ItemId> quantity);

    List<Quantity<Product>> getBasketContent();

    void removeFromBasket(Quantity<ItemId> quantity);

    ResultStatus addCustomer(CustomerInfo customer);
}
