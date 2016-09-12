package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public Optional<Authentication> getAuthentication() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public Optional<String> getCurrentUserName() {
        return getAuthentication()
                .map((auth) -> (UserDetails) auth.getPrincipal())
                .map(UserDetails::getUsername);
    }
}
