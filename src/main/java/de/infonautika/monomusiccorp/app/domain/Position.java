package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
public class Position {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @Basic
    private Long quantity;

    public Position() {
    }

    public Position(@NotNull Product product, @NotNull Long amount) {
        this.product = product;
        this.quantity = amount;
    }

    public Product getProduct() {
        return product;
    }

    public Long getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(product, position.product) &&
                Objects.equals(quantity, position.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }

    @Override
    public String toString() {
        return "Position{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }

    public static Position of(Product product, long quantity) {
        return new Position(product, quantity);
    }


}
