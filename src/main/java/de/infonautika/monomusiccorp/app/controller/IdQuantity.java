package de.infonautika.monomusiccorp.app.controller;

class IdQuantity {

    private String id;
    private Long quantity;

    public void setId(String id) {
        this.id = id;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    String getId() {
        return id;
    }

    Long getQuantity() {
        return quantity;
    }
}
