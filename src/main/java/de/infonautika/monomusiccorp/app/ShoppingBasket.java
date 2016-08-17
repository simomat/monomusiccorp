package de.infonautika.monomusiccorp.app;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public class ShoppingBasket {

    List<Position> positions = new ArrayList<>();

    public void put(ItemId itemId, Integer quantity) {
        if (positionExists(itemId)) {
            updatePositionQuantity(itemId, quantity);
        } else {
            positions.add(new Position(itemId, quantity));
        }
        removeQuantitiesBelowOne();
    }

    private void updatePositionQuantity(ItemId itemId, Integer quantity) {
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

    public void remove(ItemId itemId, Integer quantity) {
        put(itemId, -quantity);
    }

    public List<Position> getPositions() {
        return unmodifiableList(positions);
    }
}
