package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
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

import java.util.Optional;

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.*;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        StockItem stockItem = StockItem.of(product("33", "A", "T", Money.of(33d, EUR)), 40L);
        doReturn(singletonList(stockItem)).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value(HTTP_LOCALHOST + "/api/stock"));
    }

    @Test
    public void stockItemsHaveProductLink() throws Exception {
        StockItem stockItem = StockItem.of(product("33", "A", "T", Money.of(33d, EUR)), 40L);
        doReturn(singletonList(stockItem)).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..content[0]" + linkOfRel("product")).value(HTTP_LOCALHOST + "/api/catalog/33"));
    }

    @Test
    public void stockItemsHaveSelfLink() throws Exception {
        StockItem stockItem = StockItem.of(product("33", "A", "T", Money.of(33d, EUR)), 40L);
        doReturn(singletonList(stockItem)).when(stockItemRepository).findAll();

        mvc.perform(get("/api/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..content[0]" + linkOfSelf()).value(HTTP_LOCALHOST + "/api/stock/item/33"));
    }

    @Test
    public void invalidGetStockItemReturns404() throws Exception {
        doReturn(Optional.empty()).when(stockItemRepository).findByProductId(anyString());

        mvc.perform(get("/api/stock/item/55").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void singleStockItemHasSelfLink() throws Exception {
        StockItem stockItem = StockItem.of(product("33", "A", "T", Money.of(33d, EUR)), 40L);
        doReturn(Optional.of(stockItem)).when(stockItemRepository).findByProductId(anyString());

        mvc.perform(get("/api/stock/item/33").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value(HTTP_LOCALHOST + "/api/stock/item/33"));
    }

    @Test
    public void addItemToStock() throws Exception {
        mvc.perform(post("/api/stock/item/33")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":80}"))
            .andExpect(status().isNoContent());

        verify(businessProcess).addItemToStock(Quantity.of("33", 80L));
    }

    private static Product product(String id, String artist, String title, Money price) {
        Product product = Product.create(artist, title, price);
        product.setId(id);
        return product;
    }

}