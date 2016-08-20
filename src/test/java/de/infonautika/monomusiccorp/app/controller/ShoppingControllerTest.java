package de.infonautika.monomusiccorp.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShoppingControllerTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void deserializeIdQuantity() throws Exception {
        IdQuantity idQuantity = objectMapper.readValue("{\"id\":\"53\", \"quantity\":5}", IdQuantity.class);

        assertThat(idQuantity.getId(), equalTo("53"));
        assertThat(idQuantity.getQuantity(), equalTo(5L));
    }
}