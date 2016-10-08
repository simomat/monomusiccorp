package de.infonautika.monomusiccorp.app.controller;

import org.springframework.hateoas.ResourceSupport;

public class StockItemResource extends ResourceSupport {
    private Long quantity;
    private String productId;

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
