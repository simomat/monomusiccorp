package de.infonautika.monomusiccorp.app;

import java.io.Serializable;
import java.util.Objects;

public class ItemId implements Serializable {
    private String id;

    public ItemId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ItemId{" + id + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemId itemId = (ItemId) o;
        return Objects.equals(id, itemId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
