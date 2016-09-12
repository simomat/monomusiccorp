package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.ShoppingBasket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingBasketRepository extends JpaRepository<ShoppingBasket, String> {
}
