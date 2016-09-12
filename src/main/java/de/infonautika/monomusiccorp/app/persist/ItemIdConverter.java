package de.infonautika.monomusiccorp.app.persist;

import de.infonautika.monomusiccorp.app.domain.ItemId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ItemIdConverter implements AttributeConverter<ItemId, String> {
    @Override
    public String convertToDatabaseColumn(ItemId attribute) {
        return attribute.getId();
    }

    @Override
    public ItemId convertToEntityAttribute(String dbData) {
        return new ItemId(dbData);
    }
}
