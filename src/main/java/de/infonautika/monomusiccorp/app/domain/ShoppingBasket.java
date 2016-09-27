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

    public void put(ItemId itemId, Long quantity) {
        if (positionExists(itemId)) {
            updatePositionQuantity(itemId, quantity);
        } else {
            positions.add(new Position(itemId, quantity));
        }
        removeQuantitiesBelowOne();
    }

    private void updatePositionQuantity(ItemId itemId, Long quantity) {
        positions = positions.stream()
                .map(p -> p.getItemId().equals(itemId) ? new Position(itemId, p.getQuantity() + quantity) : p)
                .collect(toList());
    }

    private boolean positionExists(ItemId itemId) {
        return positions.stream().anyMatch(p -> p.getItemId().equals(itemId));
    }

    private void removeQuantitiesBelowOne() {
        positions = positions.stream()
                .filter(p -> p.getQuantity() > 0)
                .collect(toList());
    }

    public void remove(ItemId itemId, Long quantity) {
        put(itemId, -quantity);
    }

    @Override
    public List<Position> getPositions() {
        return unmodifiableList(positions);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }
}
