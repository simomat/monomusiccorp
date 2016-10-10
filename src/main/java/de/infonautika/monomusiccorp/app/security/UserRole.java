package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String STOCK_MANAGER = "STOCK_MANAGER";
    public static final String CUSTOMER = "CUSTOMER";

    public static Collection<? extends GrantedAuthority> toAuthorities(String... roles) {
        return stream(roles)
                .map(r -> (GrantedAuthority) () -> r)
                .collect(Collectors.toSet());
    }

}
