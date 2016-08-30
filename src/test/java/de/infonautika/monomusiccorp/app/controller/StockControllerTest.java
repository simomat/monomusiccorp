package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class StockControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private StockController stockController;
    @Mock
    private BusinessProcess businessProcess;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(stockController)
                .build();
    }

    @Test
    public void name() throws Exception {
        when(businessProcess.getStocks()).thenReturn(singletonList(null));

        mvc.perform(get("/stock/stock"))
                .andExpect(status().isOk())
                .andExpect(content().json("[null]"));
    }
}