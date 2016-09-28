package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "xORDER")
public class Order implements HasPositions {

    @Id
    @GeneratedValue
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ElementCollection
    private List<Position> positions;

    @Embedded
    private Address shippingAddress;

    @Column
    private LocalDateTime submitTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PickingOrder pickingOrder;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPositions(List<Position> positions) {
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
    public List<Position> getPositions() {
        return positions;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public PickingOrder getPickingOrder() {
        return pickingOrder;
    }

    public void setPickingOrder(PickingOrder pickingOrder) {
        this.pickingOrder = pickingOrder;
    }

    public String getId() {
        return id;
    }
}
