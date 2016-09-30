package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickingOrderRepository extends JpaRepository<PickingOrder, String>{
    List<PickingOrder> findByOrderCustomerId();
}
