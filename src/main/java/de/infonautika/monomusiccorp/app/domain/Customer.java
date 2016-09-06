package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private String id;

    @Basic
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }
}
