package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.Money;

public class PricedPositionResource extends PositionResource {
    private Money price;

    public void setPrice(Money price) {
        this.price = price;
    }

    public Money getPrice() {
        return price;
    }
}
