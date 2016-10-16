package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.linkOfRel;
import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.linkOfSelf;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingControllerTest {

    private Customer customer;
    private MockMvc mvc;

    @InjectMocks
    public ShoppingController shoppingController;

    @Mock
    public BusinessProcess businessProcess;

    @Mock
    public CurrentCustomerProvider currentCustomerProvider;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(shoppingController)
                .build();
        customer = new Customer();
        customer.setId("123");
        when(currentCustomerProvider.getCustomer()).thenReturn(Optional.of(customer));
    }

    @Test
    public void getBasketHasSelfLinks() throws Exception {
        Product product = Product.create("A", "T", Money.of(7.98, EUR));
        product.setId("5");
        Customer customer = new Customer();
        customer.getShoppingBasket().put(product, 2L);

        doReturn(Optional.of(customer)).when(currentCustomerProvider).getCustomer();

        mvc.perform(get("/api/basket").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..content[0]" + linkOfRel("product")).value("/api/catalog/5"))
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/basket"));
    }

    @Test
    public void putToBasketAccepted() throws Exception {
        mvc.perform(post("/api/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

        verify(businessProcess).putToBasket(customer, "5", 2L);
    }

    @Test
    public void putToBasketDefaultQuantityOne() throws Exception {
        mvc.perform(post("/api/basket/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(businessProcess).putToBasket(customer, "5", 1L);
    }

    @Test
    public void removeBasketDefaultQuantityOne() throws Exception {
        mvc.perform(delete("/api/basket/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(businessProcess).removeFromBasket(customer, "5", 1L);
    }

    @Test
    public void deleteFromBasketAccepted() throws Exception {
        mvc.perform(delete("/api/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(businessProcess).removeFromBasket(customer, "5", 2L);
    }

    @Test
    public void putNotExistingProductToBasketReturns404() throws Exception {
        doThrow(new DoesNotExistException("nope")).when(businessProcess).putToBasket(any(), any(), any());

        mvc.perform(post("/api/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}