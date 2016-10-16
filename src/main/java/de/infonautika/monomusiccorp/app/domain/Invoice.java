package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Invoice implements HasPricedPositions{

    @Id
    @GeneratedValue
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Order order;

    @Column()
    private LocalDate date;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return order.getCustomer();
    }

    @Override
    public List<PricedPosition> getPositions() {
        return order.getPositions();
    }
}
