package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, String> {
    Optional<StockItem> findByProductId(String id);
}
