package de.infonautika.monomusiccorp.app.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemIdSerializer extends JsonSerializer<ItemId> {
    @Override
    public void serialize(ItemId value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.getId());
    }
}
