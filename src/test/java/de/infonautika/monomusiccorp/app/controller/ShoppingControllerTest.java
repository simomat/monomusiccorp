package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Position;
import de.infonautika.monomusiccorp.app.domain.PricedPosition;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.intermediate.CustomerProvider;
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

import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingControllerTest {

    private final String CUSTOMER_ID = "123";
    private MockMvc mvc;

    @InjectMocks
    public ShoppingController shoppingController;

    @Mock
    public BusinessProcess businessProcess;

    @Mock
    public CustomerProvider customerProvider;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(shoppingController)
                .build();

        when(customerProvider.getCustomerId()).thenReturn(Optional.of(CUSTOMER_ID));
    }

    @Test
    public void getBasket() throws Exception {
        Product product = Product.create("A", "T", Money.of(7.98, EUR));
        product.setId("5");
        when(businessProcess.getBasketContent(CUSTOMER_ID)).thenReturn(singletonList(Position.of(product, 2L)));

        mvc.perform(get("/shopping/basket"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"product\":{\"title\":\"T\", \"artist\":\"A\", \"id\":\"5\", \"price\":{\"amount\":7.98 , \"currency\":\"EUR\"}}, \"quantity\": 2}]"));
    }

    private PricedPosition toPricedPosition(Product product, Long quantity) {
        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setProduct(product);
        pricedPosition.setPrice(product.getPrice());
        pricedPosition.setQuantity(quantity);
        return pricedPosition;
    }

    @Test
    public void putToBasket() throws Exception {

        mvc.perform(put("/shopping/basket/put")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item\": \"34\", \"quantity\": 5}"))
                .andExpect(status().isOk());

        verify(businessProcess).putToBasket(CUSTOMER_ID, Quantity.of("34", 5L));
    }

    @Test
    public void removeFromBasket() throws Exception {
        mvc.perform(delete("/shopping/basket/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item\": \"34\", \"quantity\": 1}"))
                .andExpect(status().isOk());

        verify(businessProcess).removeFromBasket(CUSTOMER_ID, Quantity.of("34", 1L));

    }
}