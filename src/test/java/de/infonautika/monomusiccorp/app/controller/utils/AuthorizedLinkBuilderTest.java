package de.infonautika.monomusiccorp.app.controller.utils;

import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizedLinkBuilderTest {

    @InjectMocks
    public AuthorizedLinkBuilder authorizedLinkBuilder;

    @Mock
    public AuthenticationFacade authenticationFacade;

    @Test
    public void linkUnrestrictedMethodsAlwaysGranted() throws Exception {
        ControllerLinkBuilderConsumer controllerLinkBuilderConsumer = new ControllerLinkBuilderConsumer();

        authorizedLinkBuilder.withRightsOn(
                methodOn(DummyController.class).unrestrictedDummyMethod(),
                controllerLinkBuilderConsumer
        );

        assertThat(controllerLinkBuilderConsumer.wasCalled, is(true));
    }

    @Test
    public void linkSecuredMethodsWithMissingRolesDenied() throws Exception {
        setCurrentUserRoles("ROLE_SOMETHING_ELSE");

        ControllerLinkBuilderConsumer controllerLinkBuilderConsumer = new ControllerLinkBuilderConsumer();
        authorizedLinkBuilder.withRightsOn(
                methodOn(DummyController.class).restrictedDummyMethod(),
                controllerLinkBuilderConsumer
        );

        assertThat(controllerLinkBuilderConsumer.wasCalled, is(false));
    }

    @Test
    public void linkSecuredMethodsWithPresentRolesGranted() throws Exception {
        setCurrentUserRoles("ROLE_SOMETHING_ELSE", "ROLE_TESTER");

        ControllerLinkBuilderConsumer controllerLinkBuilderConsumer = new ControllerLinkBuilderConsumer();
        authorizedLinkBuilder.withRightsOn(
                methodOn(DummyController.class).restrictedDummyMethod(),
                controllerLinkBuilderConsumer
        );

        assertThat(controllerLinkBuilderConsumer.wasCalled, is(true));
    }

    private void setCurrentUserRoles(String... roles) {
        doReturn(stream(roles)
                .map(role -> (GrantedAuthority) () -> role)
                .collect(Collectors.toList()))
            .when(authenticationFacade).getCurrentUserAuthorities();
    }

    private class ControllerLinkBuilderConsumer implements Consumer<DummyInvocationUtils.LastInvocationAware> {
        private boolean wasCalled = false;
        @Override
        public void accept(DummyInvocationUtils.LastInvocationAware invocationAware) {
            wasCalled = true;
        }
    }


    private class DummyController {
        public DummyController() {
        }

        public Number unrestrictedDummyMethod() {
            return null;
        }

        @Secured({"ROLE_TESTER", "ROLE_ADMIN"})
        public Number restrictedDummyMethod() {
            return null;
        }
    }
}