package de.infonautika.monomusiccorp.app.business;

import java.util.Objects;

public class Quantity<T> {
    private T item;
    private Long quantity;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public static <T> Quantity<T> create(T item, Long quantity) {
        Quantity<T> tQuantity = new Quantity<>();
        tQuantity.setItem(item);
        tQuantity.setQuantity(quantity);
        return tQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> quantity1 = (Quantity<?>) o;
        return Objects.equals(item, quantity1.item) &&
                Objects.equals(quantity, quantity1.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity);
    }

    @Override
    public String toString() {
        return "Quantity{" +
                "item=" + item +
                ", quantity=" + quantity +
                '}';
    }
}