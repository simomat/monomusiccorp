package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, String> {
    StockItem findByProductId(String id);
}
