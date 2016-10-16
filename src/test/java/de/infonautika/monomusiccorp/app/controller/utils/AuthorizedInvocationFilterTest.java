package de.infonautika.monomusiccorp.app.controller.utils;

import de.infonautika.monomusiccorp.app.controller.utils.links.Invocation;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizedInvocationFilterTest {

    @InjectMocks
    public AuthorizedInvocationFilter authorizedInvocationFilter;

    @Mock
    public AuthenticationFacade authenticationFacade;

    @Test
    public void linkUnrestrictedMethodsAlwaysGranted() throws Exception {
        ControllerInvocationConsumer controllerInvocationConsumer = new ControllerInvocationConsumer();

        authorizedInvocationFilter.withRightsOn(
                methodOn(DummyController.class).unrestrictedDummyMethod(),
                controllerInvocationConsumer
        );

        assertThat(controllerInvocationConsumer.wasCalled, is(true));
    }

    @Test
    public void linkSecuredMethodsWithMissingRolesDenied() throws Exception {
        setCurrentUserRoles("ROLE_SOMETHING_ELSE");

        ControllerInvocationConsumer controllerInvocationConsumer = new ControllerInvocationConsumer();
        authorizedInvocationFilter.withRightsOn(
                methodOn(DummyController.class).restrictedDummyMethod(),
                controllerInvocationConsumer
        );

        assertThat(controllerInvocationConsumer.wasCalled, is(false));
    }

    @Test
    public void linkSecuredMethodsWithPresentRolesGranted() throws Exception {
        setCurrentUserRoles("ROLE_SOMETHING_ELSE", "ROLE_TESTER");

        ControllerInvocationConsumer controllerInvocationConsumer = new ControllerInvocationConsumer();
        authorizedInvocationFilter.withRightsOn(
                methodOn(DummyController.class).restrictedDummyMethod(),
                controllerInvocationConsumer
        );

        assertThat(controllerInvocationConsumer.wasCalled, is(true));
    }

    private void setCurrentUserRoles(String... roles) {
        doReturn(stream(roles)
                .map(role -> (GrantedAuthority) () -> role)
                .collect(Collectors.toList()))
            .when(authenticationFacade).getCurrentUserAuthorities();
    }

    private class ControllerInvocationConsumer implements Consumer<Invocation> {
        private boolean wasCalled = false;
        @Override
        public void accept(Invocation invocationAware) {
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