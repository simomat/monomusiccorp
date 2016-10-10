package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

public interface AuthenticationFacade {
    Optional<Authentication> getAuthentication();

    Collection<? extends GrantedAuthority> getCurrentUserAuthorities();

    Optional<String> getCurrentUserName();
}
