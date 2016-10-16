package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

@Entity
public class ShoppingBasket implements HasPositions {

    @Id
    @GeneratedValue
    private String id;

    @ElementCollection
    private List<Position> positions = new ArrayList<>();

    public void put(Product product, Long quantity) {
        if (positionExists(product)) {
            updatePositionQuantity(product, quantity);
        } else {
            positions.add(new Position(product, quantity));
        }
        removeQuantitiesBelowOne();
    }

    private void updatePositionQuantity(Product product, Long quantity) {
        positions = positions.stream()
                .map(p -> p.getProduct().getId().equals(product.getId()) ? new Position(product, p.getQuantity() + quantity) : p)
                .collect(toList());
    }

    private boolean positionExists(Product product) {
        return positions.stream().anyMatch(p -> p.getProduct().getId().equals(product.getId()));
    }

    private void removeQuantitiesBelowOne() {
        positions = positions.stream()
                .filter(p -> p.getQuantity() > 0)
                .collect(toList());
    }

    public void remove(String productId, Long quantity) {
        positions.stream()
                .map(Position::getProduct)
                .filter(prod -> prod.getId().equals(productId))
                .findFirst()
                .ifPresent(prod -> remove(prod, quantity));
    }

    public void remove(Product product, Long quantity) {
        put(product, -quantity);
    }

    @Override
    public List<Position> getPositions() {
        return unmodifiableList(positions);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public void clear() {
        positions = new ArrayList<>();
    }
}
