package de.infonautika.monomusiccorp.app.controller;

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
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
    }

    @Test
    public void usernameResourceHasSelfLink() throws Exception {
        doReturn(Optional.of("blubbi")).when(authenticationFacade).getCurrentUserName();

        mvc.perform(get("/api/user/currentuser").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$" + linkOfSelf()).value("/api/user/currentuser"));
    }
}