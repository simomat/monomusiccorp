package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.links.Invocation;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.intermediate.CustomerProvider;
import de.infonautika.monomusiccorp.app.repository.PickingOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.linkOfRel;
import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.linkOfSelf;
import static de.infonautika.monomusiccorp.app.controller.MatcherDebug.debug;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Mock
    public PickingOrderRepository pickingOrderRepository;

    @Mock
    public AuthorizedInvocationFilter authorizedInvocationFilter;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(shoppingController)
                .build();

        when(customerProvider.getCustomerId()).thenReturn(Optional.of(CUSTOMER_ID));
        initLinkBuilderAlwaysAuthorized();
    }

    private void initLinkBuilderAlwaysAuthorized() {
        doAnswer(invocation -> {
            Invocation invocationArgument = invocation.getArgument(0);
            Consumer<Invocation> invocationAwareConsumer = invocation.getArgument(1);
            invocationAwareConsumer.accept(invocationArgument);
            return null;
        }).when(authorizedInvocationFilter).withRightsOn(any(), any());
    }

    @Test
    public void getBasketHasSelfLinks() throws Exception {
        Product product = Product.create("A", "T", Money.of(7.98, EUR));
        product.setId("5");
        when(businessProcess.getBasketContent(CUSTOMER_ID)).thenReturn(singletonList(Position.of(product, 2L)));

        mvc.perform(get("/api/shopping/basket").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(debug(jsonPath("$..content[0]" + linkOfRel("product")).value("/api/catalog/5")))
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/shopping/basket"));
    }

    @Test
    public void putToBasketAccepted() throws Exception {
        mvc.perform(post("/api/shopping/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

        verify(businessProcess).putToBasket(CUSTOMER_ID, "5", 2L);

    }

    @Test
    public void deleteFromBasketAccepted() throws Exception {
        mvc.perform(delete("/api/shopping/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(businessProcess).removeFromBasket(CUSTOMER_ID, "5", 2L);

    }

    @Test
    public void putNotExistingProductToBasketReturns404() throws Exception {
        doThrow(new DoesNotExistException("nope")).when(businessProcess).putToBasket(any(), any(), any());

        mvc.perform(post("/api/shopping/basket/5?quantity=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrdersHasSelfLinks() throws Exception {
        Product product = Product.create("A", "T", Money.of(7.98, EUR));
        product.setId("5");

        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setPrice(Money.of(2d, EUR));
        pricedPosition.setProduct(product);
        pricedPosition.setQuantity(2L);

        Order order = new Order();
        order.setPositions(singletonList(pricedPosition));
        order.setSubmitTime(LocalDateTime.now());

        PickingOrder pickingOrder = new PickingOrder();
        pickingOrder.setStatus(PickingOrder.PickingStatus.OPEN);
        pickingOrder.setOrder(order);

        doReturn(singletonList(pickingOrder)).when(pickingOrderRepository).findByOrderCustomerId(anyString());

        mvc.perform(get("/api/shopping/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(debug(jsonPath("$..positions[0]" + linkOfRel("product")).value("/api/catalog/5")))
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/shopping/orders"));
    }

}