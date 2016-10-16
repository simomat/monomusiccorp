package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.errors.ConflictException;
import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
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

import static de.infonautika.monomusiccorp.app.controller.ControllerConstants.linkOfSelf;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest {

    private MockMvc mvc;

    @InjectMocks
    public CustomerController customerController;

    @Mock
    private CustomerLookup customerLookup;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private BusinessProcess businessProcess;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(customerController)
                .build();

    }

    @Test
    public void getAllHasSelfLink() throws Exception {
        doReturn(emptyList()).when(customerLookup).findAll();

        mvc.perform(get("/api/customer/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/customer/all"));
    }

    @Test
    public void getCustomerHasSelfLink() throws Exception {
        Customer customer = new Customer();
        customer.setUsername("hans");
        doReturn(Optional.of(customer)).when(customerLookup).getCustomerByName(any());

        mvc.perform(get("/api/customer/hans").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/customer/hans"));
    }

    @Test
    public void registerSavesCustomer() throws Exception {
        mvc.perform(post("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"hans\", \"password\":\"hansi\", \"address\":\"lagistrasse 35\"}"))
                .andExpect(status().isNoContent());

        verify(businessProcess).addCustomer(argThat(customerInfo -> customerInfo.getUsername().equals("hans")));

    }

    @Test
    public void registerHandlesConflict() throws Exception {
        doThrow(ConflictException.class).when(businessProcess).addCustomer(any());

        mvc.perform(post("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"hans\", \"password\":\"hansi\", \"address\":\"lagistrasse 35\"}"))
                .andExpect(status().isConflict());
    }
}