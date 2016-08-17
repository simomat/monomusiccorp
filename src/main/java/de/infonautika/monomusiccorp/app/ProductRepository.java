package de.infonautika.monomusiccorp.app;

import de.infonautika.monomusiccorp.app.ItemId;
import de.infonautika.monomusiccorp.app.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, ItemId> {

}
