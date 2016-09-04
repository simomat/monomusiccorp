package de.infonautika.monomusiccorp.app.serialize;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ItemIdDeserializerTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void deserializeCorrectly() throws Exception {
        ItemId itemId = objectMapper.readValue("{\"itemId\": \"42\", \"asd\":2}", ItemId.class);

        assertThat(itemId.getId(), equalTo("42"));
    }

    @Test(expected = JsonParseException.class)
    public void failOnNoIdGiven() throws Exception {
        objectMapper.readValue("{\"asd\":2}", ItemId.class);
    }

    @Test(expected = JsonParseException.class)
    public void failOnIdIsNoString() throws Exception {
        objectMapper.readValue("{\"itemId\": {\"x\":2}}", ItemId.class);
    }
}