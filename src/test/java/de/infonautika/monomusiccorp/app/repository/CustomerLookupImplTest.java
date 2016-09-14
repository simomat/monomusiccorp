package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.ShoppingBasket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerLookupImplTest {

    @InjectMocks
    public CustomerLookupImpl customerLookup;

    @Mock
    public CustomerRepository customerRepository;

    @Test
    public void getShoppingBasketOfCustomer() throws Exception {
        Customer customer = new Customer();
        ShoppingBasket shoppingBasket = customer.getShoppingBasket();
        when(customerRepository.findById("3")).thenReturn(customer);

        assertThat(customerLookup.getShoppingBasketOfCustomer("3"), is(Optional.of(shoppingBasket)));
    }
}