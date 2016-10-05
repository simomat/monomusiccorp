package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "xORDER")
public class Order implements HasPricedPositions {

    @Id
    @GeneratedValue
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ElementCollection
    private List<PricedPosition> positions;

    @Embedded
    private Address shippingAddress;

    @Column
    private LocalDateTime submitTime;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPositions(List<PricedPosition> positions) {
        this.positions = positions;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    @Override
    public List<PricedPosition> getPositions() {
        return positions;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }
}
