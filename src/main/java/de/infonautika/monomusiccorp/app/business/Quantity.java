package de.infonautika.monomusiccorp.app.business;

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
}