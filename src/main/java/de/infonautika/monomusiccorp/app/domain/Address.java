package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {

    @Basic
    private String addressString;

    public Address() {
    }

    public Address(String addressString) {
        this.addressString = addressString;
    }

    public String getAddressString() {
        return addressString;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressString='" + addressString + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressString, address.addressString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressString);
    }
}
