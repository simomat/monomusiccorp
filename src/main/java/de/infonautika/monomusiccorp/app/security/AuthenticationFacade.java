package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AuthenticationFacade {
    Optional<Authentication> getAuthentication();

    Optional<String> getCurrentUserName();
}
