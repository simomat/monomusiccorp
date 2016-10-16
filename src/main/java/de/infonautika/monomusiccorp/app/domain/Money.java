package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Money {
    public static final Money ZERO = Money.of(0d, Currencies.EUR);
    @Basic
    private Double amount;
    @Basic
    private String currency;

    public static Money of(Double amount, String currency) {
        Money money = new Money();
        money.setAmount(amount);
        money.setCurrency(currency);
        return money;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Money{"+ amount + " " + currency + '}';
    }
}
