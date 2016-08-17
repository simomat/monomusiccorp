package de.infonautika.monomusiccorp.app;

import de.infonautika.monomusiccorp.app.domain.ItemId;

public class ItemIdCreator {

    private static Integer nextId = 0;

    public static ItemId createItemId() {
        return new ItemId((nextId++).toString());
    }
}
