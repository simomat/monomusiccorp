package de.infonautika.monomusiccorp.app;

import javax.persistence.*;

@Entity
public class StockItem {

    @Id
    @GeneratedValue
    private String id;
    @OneToOne
    private Product product;
    @Basic
    private Long quantity;

    public static StockItem create(Product product, Long quantity) {
        StockItem stockItem = new StockItem();
        stockItem.setProduct(product);
        stockItem.setQuantity(quantity);
        return stockItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
