package de.infonautika.monomusiccorp.app.controller.utils;

import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

@Service
public class AuthorizedLinkBuilder {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public void withRightsOn(Object invocationValue, Consumer<ControllerLinkBuilder> linkConsumer) {
        Assert.isInstanceOf(DummyInvocationUtils.LastInvocationAware.class, invocationValue);

        Method method = ((DummyInvocationUtils.LastInvocationAware) invocationValue).getLastInvocation().getMethod();

        Optional.ofNullable(method.getAnnotation(Secured.class))
                .filter(secured -> currentUserHasRole(secured.value()))
                .ifPresent(secured -> linkConsumer.accept(ControllerLinkBuilder.linkTo(invocationValue)));
    }

    private boolean currentUserHasRole(String[] roles) {
        return authenticationFacade.getAuthentication()
                .map(authentication -> authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> hasAnyRole(grantedAuthority, roles)))
                .orElse(false);
    }

    private boolean hasAnyRole(GrantedAuthority authority, String[] roles) {
        String authorityString = authority.getAuthority();
        return stream(roles).anyMatch(role -> role.equalsIgnoreCase(authorityString));
    }
}
