package de.infonautika.monomusiccorp.app.intermediate;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCustomerProviderImplTest {

    @InjectMocks
    public CurrentCustomerProviderImpl customerProvider;

    @Mock
    public AuthenticationFacade authenticationFacade;

    @Mock
    public CustomerLookup customerLookup;

    @Test
    public void getCustomerId() throws Exception {
        Customer customer = new Customer();
        customer.setId("1");
        when(authenticationFacade.getCurrentUserName()).thenReturn(Optional.of("Jens"));
        when(customerLookup.getCustomerByName("Jens")).thenReturn(Optional.of(customer));

        assertThat(customerProvider.getCustomerId(), is(Optional.of("1")));
    }
}