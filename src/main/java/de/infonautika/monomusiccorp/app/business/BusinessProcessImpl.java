package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.Arrays.asList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

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
}
