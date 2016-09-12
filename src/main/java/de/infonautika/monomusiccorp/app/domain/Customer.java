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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public ShoppingBasket getShoppingBasket() {
        return shoppingBasket;
    }

}
