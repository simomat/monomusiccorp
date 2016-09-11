package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

import static de.infonautika.monomusiccorp.app.security.UserRole.toAuthorities;

public class MutableUserDetails implements UserDetails{
    private static final String ROLE_PREFIX = "ROLE_";
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
        return new HashSet<>(authorities);
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
