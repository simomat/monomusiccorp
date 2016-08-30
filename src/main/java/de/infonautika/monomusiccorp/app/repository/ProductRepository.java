package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, ItemId> {
    List<Product> findByIdIn(Collection<String> ids);
}
