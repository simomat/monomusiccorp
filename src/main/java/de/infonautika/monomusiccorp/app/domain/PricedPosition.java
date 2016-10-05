package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class PricedPosition {

    @Embedded
    private Money price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Basic
    private Long quantity;

    public Money getPrice() {
        return price;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public static PricedPosition of(Product product, Long quantity, Money price) {
        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setProduct(product);
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
                Objects.equals(product, that.product) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, product, quantity);
    }

    @Override
    public String toString() {
        return "PricedPosition{" +
                "price=" + price +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
