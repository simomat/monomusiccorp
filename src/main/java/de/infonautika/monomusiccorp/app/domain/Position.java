package de.infonautika.monomusiccorp.app.domain;

import de.infonautika.monomusiccorp.app.persist.ItemIdConverter;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
public class Position {

    @Basic
    @Convert(converter = ItemIdConverter.class)
    private ItemId itemId;
    @Basic
    private Long quantity;

    public Position() {
    }

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

    public static Position of(ItemId itemId, long quantity) {
        return new Position(itemId, quantity);
    }
}
