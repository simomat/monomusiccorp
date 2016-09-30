package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class PricedPosition {

    @Embedded
    private Money price;

    @Basic
    private ItemId itemId;

    @Basic
    private Long quantity;

    public Money getPrice() {
        return price;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public static PricedPosition of(ItemId itemId, Long quantity, Money price) {
        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setItemId(itemId);
        pricedPosition.setQuantity(quantity);
        pricedPosition.setPrice(price);
        return pricedPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricedPosition that = (PricedPosition) o;
        return Objects.equals(price, that.price) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, itemId, quantity);
    }

    @Override
    public String toString() {
        return "PricedPosition{" +
                "price=" + price +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }
}
