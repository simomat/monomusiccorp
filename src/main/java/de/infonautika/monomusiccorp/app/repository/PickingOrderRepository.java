package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickingOrderRepository extends JpaRepository<PickingOrder, String>{
}
