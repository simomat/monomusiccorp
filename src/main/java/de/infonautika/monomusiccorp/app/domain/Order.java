package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "xORDER")
public class Order {

    @Id
    @GeneratedValue
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Customer customer;

    @ElementCollection
    private List<Position> positions;

    @Embedded
    private Address shippingAddress;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}