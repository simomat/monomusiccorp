package de.infonautika.monomusiccorp.app;

import de.infonautika.monomusiccorp.app.Product;

import java.util.Collection;

public interface BusinessProcess {
    void createDatabase();

    Collection<Product> getAllProducts();
}
