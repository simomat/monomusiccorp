package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.*;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class StockControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private StockController stockController;
    @Mock
    private BusinessProcess businessProcess;

    @Mock
    private StockItemRepository stockItemRepository;


    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(stockController)

                .build();
    }

    @Test
    public void noItemsReturnsGetsResult() throws Exception {
        doReturn(emptyList()).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void getStockHasSelfLink() throws Exception {
        Product product = Product.create("A", "T", Money.of(33d, EUR));
        product.setId("33");
        StockItem stockItem = StockItem.of(product, 40L);
        doReturn(singletonList(stockItem)).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + LINKS_SELF_HREF).value(HTTP_LOCALHOST + "/api/stock"));
    }


    @Test
    public void stockItemsHaveProductLink() throws Exception {
        Product product = Product.create("A", "T", Money.of(33d, EUR));
        product.setId("33");
        StockItem stockItem = StockItem.of(product, 40L);
        doReturn(singletonList(stockItem)).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..content[0]" + linkOfRel("prod")).value(HTTP_LOCALHOST + "/api/catalog/33"));
    }

}