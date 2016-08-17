package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.Product;

import java.util.Collection;

public interface BusinessProcess {
    void createDatabase();

    Collection<Product> getAllProducts();
}
