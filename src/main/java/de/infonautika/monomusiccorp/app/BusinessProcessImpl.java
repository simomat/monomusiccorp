package de.infonautika.monomusiccorp.app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.Arrays.asList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    @Autowired
    ProductRepository productRepo;

    @Override
    public void createDatabase() {

        Product[] products = {
                Product.create("AC/DC", "Back in Black"),
                Product.create("The Byrds", "Fifth Dimension "),
                Product.create("AC/DC", "Let There Be Rock "),
                Product.create("Jefferson Airplane", "Surrealistic Pillow"),
                Product.create("The Easybeats", "Good Friday/Friday On My Mind")
        };
/*
        StockItem[] stocks = {
                StockItem.create(products[0], 20L),
                StockItem.create(products[1], 15L),
                StockItem.create(products[2], 3L)
        };
*/
        productRepo.save(asList(products));



    }

    @Override
    public Collection<Product> getAllProducts() {
        return productRepo.findAll();
    }
}
