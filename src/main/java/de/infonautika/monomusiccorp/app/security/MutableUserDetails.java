package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static de.infonautika.monomusiccorp.app.security.UserRole.toAuthorities;
import static java.util.Collections.unmodifiableCollection;

public class MutableUserDetails implements UserDetails{
    private final String username;
    private String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private MutableUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public MutableUserDetails(UserDetails user) {
        username = user.getUsername();
        password = user.getPassword();
        authorities = user.getAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public static MutableUserDetails create(String username, String password, String... roles) {
        return new MutableUserDetails(
                username,
                password,
                toAuthorities(roles));
    }
}
