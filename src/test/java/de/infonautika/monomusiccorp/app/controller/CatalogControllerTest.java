package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.repository.ProductLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CatalogControllerTest {

    public static final String HTTP_LOCALHOST = "http://localhost";
    public static final String LINKS_SELF_HREF = ".links[?(@.rel=='self')].href";
    private MockMvc mvc;

    @InjectMocks
    public CatalogController catalogController;

    @Mock
    public ProductLookup productLookup;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(catalogController)
                    .build();
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
                .andExpect(jsonPath("$" + LINKS_SELF_HREF).value(HTTP_LOCALHOST + "/api/catalog"))
                .andExpect(jsonPath("$..content[0]" + LINKS_SELF_HREF).value(HTTP_LOCALHOST + "/api/catalog/22"));
    }

}