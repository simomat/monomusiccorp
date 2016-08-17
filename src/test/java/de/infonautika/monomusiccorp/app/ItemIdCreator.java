package de.infonautika.monomusiccorp.app;

public class ItemIdCreator {

    private static Integer nextId = 0;

    public static ItemId createItemId() {
        return new ItemId((nextId++).toString());
    }
}
