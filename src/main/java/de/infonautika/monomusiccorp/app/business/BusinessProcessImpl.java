package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.OperationNotSupportedException;
import java.util.Collection;
import java.util.List;

import static de.infonautika.streamjoin.Join.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

    ShoppingBasket shoppingBasket = new ShoppingBasket();

    @Override
    public void createDatabase() {

        Product[] products = {
                Product.create("AC/DC", "Back in Black"),
                Product.create("The Byrds", "Fifth Dimension "),
                Product.create("AC/DC", "Let There Be Rock "),
                Product.create("Jefferson Airplane", "Surrealistic Pillow"),
                Product.create("The Easybeats", "Good Friday/Friday On My Mind")
        };

        StockItem[] stocks = {
                StockItem.create(products[0], 20L),
                StockItem.create(products[1], 15L),
                StockItem.create(products[2], 3L)
        };

        productRepo.save(asList(products));
        stockItemRepository.save(asList(stocks));
    }

    @Override
    public Collection<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public void addItemToStock(ItemId itemId, Long count) {
        StockItem stockItem = stockItemRepository.findByProductId(itemId.getId());
        stockItem.setQuantity(stockItem.getQuantity()+count);
        stockItemRepository.save(stockItem);
    }

    @Override
    public Collection<StockItem> getStocks() {
        return stockItemRepository.findAll();
    }

    @Override
    public void putToBasket(Quantity<ItemId> quantity) {
        shoppingBasket.put(quantity.getItem(), quantity.getQuantity());
    }

    @Override
    public List<Quantity<Product>> getBasketContent() {
        List<Position> positions = shoppingBasket.getPositions();
        List<String> ids = positions.stream()
                .map(p -> p.getItemId().getId())
                .collect(toList());
        List<Product> products = productRepo.findByIdIn(ids);

        return join(positions.stream())
                .withKey(Position::getItemId)
                .on(products.stream())
                .withKey(Product::getItemId)
                .combine((pos, prod) -> Quantity.create(prod, pos.getQuantity()))
                .collect(toList());
    }

    @Override
    public void removeFromBasket(Quantity<ItemId> quantity) {
        shoppingBasket.remove(quantity.getItem(), quantity.getQuantity());
    }


}
