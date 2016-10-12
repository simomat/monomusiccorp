package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.Invocation;
import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.repository.ProductLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.function.Consumer;

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.*;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CatalogControllerTest {

    private MockMvc mvc;

    @InjectMocks
    public CatalogController catalogController;

    @Mock
    public ProductLookup productLookup;

    @Mock
    public AuthorizedInvocationFilter authorizedInvocationFilter;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(catalogController)
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
    public void noProductsReturnsEmptyResult() throws Exception {
        doReturn(emptyList()).when(productLookup).findAll();

        mvc.perform(get("/api/catalog").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void getAllProductsHasSelfLinks() throws Exception {
        Product product = Product.create("A", "T", Money.of(22D, EUR));
        product.setId("22");
        doReturn(singletonList(product)).when(productLookup).findAll();

        mvc.perform(get("/api/catalog").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/catalog"))
                .andExpect(jsonPath("$..content[0]" + linkOfSelf()).value("/api/catalog/22"));
    }

    @Test
    public void invalidProductIdGets404() throws Exception {
        doReturn(Optional.empty()).when(productLookup).findOne(anyString());

        mvc.perform(get("/api/catalog/33").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void singleProductHasSelfLink() throws Exception {
        Product product = Product.create("A", "T", Money.of(22D, EUR));
        product.setId("22");
        doReturn(Optional.of(product)).when(productLookup).findOne(anyString());

        mvc.perform(get("/api/catalog/22").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/catalog/22"));
    }

    @Test
    public void singleProductHasAddToStockLink() throws Exception {
        Product product = Product.create("A", "T", Money.of(22D, EUR));
        product.setId("22");
        doReturn(Optional.of(product)).when(productLookup).findOne(anyString());

        mvc.perform(get("/api/catalog/22").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfRel("stock")).exists());
    }

}