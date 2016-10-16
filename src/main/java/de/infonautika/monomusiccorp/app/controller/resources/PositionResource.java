package de.infonautika.monomusiccorp.app.controller.resources;

import org.springframework.hateoas.ResourceSupport;

public class PositionResource extends ResourceSupport {
    private String productId;
    private Long quantity;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }
}
