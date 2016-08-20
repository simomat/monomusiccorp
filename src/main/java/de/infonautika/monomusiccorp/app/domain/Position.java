package de.infonautika.monomusiccorp.app.domain;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Position {
    final private ItemId itemId;
    final private Long quantity;

    public Position(@NotNull ItemId itemId, @NotNull Long amount) {
        this.itemId = itemId;
        this.quantity = amount;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Position update(long newQuantity) {
        return new Position(itemId, newQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(itemId, position.itemId) &&
                Objects.equals(quantity, position.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, quantity);
    }

    @Override
    public String toString() {
        return "Position{" +
                "itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }
}
