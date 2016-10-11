package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.domain.Address;
import org.springframework.hateoas.ResourceSupport;

public class CustomerResource extends ResourceSupport {
    private String customerId;
    private String username;
    private Address address;

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
