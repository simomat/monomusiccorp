package de.infonautika.monomusiccorp.app.controller;

class StockItemSupply {
    private String id;

    Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Long quantity;

}
