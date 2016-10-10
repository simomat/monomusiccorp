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
import java.util.function.Function;

import static java.util.Arrays.stream;

@Service
public class AuthorizedLinkBuilder {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    private Function<Object, ControllerLinkBuilder> linkBuilder = ControllerLinkBuilder::linkTo;

    void setLinkBuilder(Function<Object, ControllerLinkBuilder> linkBuilder) {
        this.linkBuilder = linkBuilder;
    }

    public void withRightsOn(Object invocationValue, Consumer<ControllerLinkBuilder> linkConsumer) {
        Assert.isInstanceOf(DummyInvocationUtils.LastInvocationAware.class, invocationValue);

        Method method = ((DummyInvocationUtils.LastInvocationAware) invocationValue).getLastInvocation().getMethod();

        if (accessGranted(method)) {
            linkConsumer.accept(linkBuilder.apply(invocationValue));
        }
    }

    private Boolean accessGranted(Method method) {
        return getAllowedRoles(method)
                .map(this::currentUserHasRole)
                .orElse(true);
    }

    private Optional<String[]> getAllowedRoles(Method method) {
        return Optional.ofNullable(method.getAnnotation(Secured.class))
                .map(secured -> Optional.of(secured.value()))
                .orElse(Optional.empty());
    }

    private boolean currentUserHasRole(String[] roles) {
        return authenticationFacade.getCurrentUserAuthorities().stream()
                    .anyMatch(grantedAuthority -> hasAnyRole(grantedAuthority, roles));
    }

    private boolean hasAnyRole(GrantedAuthority authority, String[] roles) {
        String authorityString = authority.getAuthority();
        return stream(roles).anyMatch(role -> role.equalsIgnoreCase(authorityString));
    }
}
