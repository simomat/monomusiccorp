package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.stream.Stream;

public interface ProductRepository extends JpaRepository<Product, String> {
    Stream<Product> findByIdIn(Collection<String> ids);

}
