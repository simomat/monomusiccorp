package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;

import java.util.Collection;
import java.util.List;

public interface BusinessProcess {
    void createDatabase();

    Collection<Product> getAllProducts();

    void addItemToStock(ItemId itemId, Long count);

    Collection<StockItem> getStocks();

    void putToBasket(Quantity<ItemId> quantity);

    List<Quantity<Product>> getBasketContent();

    void removeFromBasket(Quantity<ItemId> quantity);
}
