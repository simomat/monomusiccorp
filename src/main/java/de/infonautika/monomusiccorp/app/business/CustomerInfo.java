package de.infonautika.monomusiccorp.app.business;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.infonautika.monomusiccorp.app.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerInfo implements UserDetails {

    private String userName;
    private String password;
    private String address;

    @JsonCreator
    public CustomerInfo(
            @JsonProperty(value = "userName", required = true) String userName,
            @JsonProperty(value = "password", required = true) String password,
            @JsonProperty(value = "address", required = true) String address) {
        this.userName = userName;
        this.password = password;
        this.address = address;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserRole.toAuthorities(UserRole.CUSTOMER);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getAddress() {
        return address;
    }
}
