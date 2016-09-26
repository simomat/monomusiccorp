package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class PickingOrder {

    @Id
    @GeneratedValue
    private String id;

    @ElementCollection
    private List<Position> pickedItems;

    @Enumerated(EnumType.STRING)
    private PickingStatus status;
    private Order order;

    public void setStatus(PickingStatus status) {
        this.status = status;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPickedItems(List<Position> pickedItems) {
        this.pickedItems = pickedItems;
    }

    public PickingStatus getStatus() {
        return status;
    }

    public enum PickingStatus {OPEN}
}
