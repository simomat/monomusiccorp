package de.infonautika.monomusiccorp.app.serialize;


import de.infonautika.monomusiccorp.app.JsonTesterRule;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ItemIdSerializerTest {

    @Rule
    public JsonTesterRule<ItemId> json = new JsonTesterRule<>();

    @Test
    public void serializesCorrectly() throws Exception {
        assertThat(json.write(new ItemId("23"))).isEqualToJson("{\"id\": \"23\"}");
    }
}