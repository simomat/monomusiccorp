package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.links.Invocation;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.intermediate.CurrentCustomerProvider;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class MyOrdersControllerTest {

    private MockMvc mvc;

    @InjectMocks
    public MyOrdersController myOrdersController;

    @Mock
    public PickingOrderRepository pickingOrderRepository;

    @Mock
    public CurrentCustomerProvider currentCustomerProvider;

    @Mock
    public AuthorizedInvocationFilter authorizedInvocationFilter;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(myOrdersController)
                .build();
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

        doReturn(Optional.of("CUSTOMER_ID")).when(currentCustomerProvider).getCustomerId();
        doReturn(singletonList(pickingOrder)).when(pickingOrderRepository).findByOrderCustomerId(anyString());

        mvc.perform(get("/api/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(debug(jsonPath("$..positions[0]" + linkOfRel("product")).value("/api/catalog/5")))
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/orders"));
    }
}