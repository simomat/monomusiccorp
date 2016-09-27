package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.stream.Stream;

public interface ProductRepository extends JpaRepository<Product, String> {

    // EclipseLink still has a bug when building sql with IN-criteria, see DATAJPA-433 :(
    // so we override sql building by passing an own query
    // careful: this fails when passing en empty collection
    @Query(value = "select p from Product p where p.id in :ids")
    Stream<Product> findByIdIn(@Param("ids") Collection<String> ids);

}
