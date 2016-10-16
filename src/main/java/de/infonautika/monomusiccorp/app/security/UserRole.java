package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UserRole {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String STOCK_MANAGER = "ROLE_STOCK_MANAGER";
    public static final String CUSTOMER = "ROLE_CUSTOMER";

    public static Collection<? extends GrantedAuthority> toAuthorities(String... roles) {
        return stream(roles)
                .map(r -> (GrantedAuthority) () -> r)
                .collect(Collectors.toSet());
    }

}
