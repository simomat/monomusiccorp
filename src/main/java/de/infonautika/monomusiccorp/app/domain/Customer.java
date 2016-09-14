package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private String id;

    @Column(unique=true)
    private String username;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ShoppingBasket shoppingBasket = new ShoppingBasket();

    @Embedded
    private Address address;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public ShoppingBasket getShoppingBasket() {
        return shoppingBasket;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }
}
