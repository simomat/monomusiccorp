package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private String id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ShoppingBasket shoppingBasket = new ShoppingBasket();

    @Embedded
    private Address address;

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

    public void setId(String id) {
        this.id = id;
    }

    public void setShoppingBasket(ShoppingBasket shoppingBasket) {
        this.shoppingBasket = shoppingBasket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
