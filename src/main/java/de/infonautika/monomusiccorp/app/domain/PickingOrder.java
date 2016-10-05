package de.infonautika.monomusiccorp.app.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class PickingOrder implements HasPositions {

    @Id
    @GeneratedValue
    private String id;

    @ElementCollection
    private List<Position> pickedItems;

    @Enumerated(EnumType.STRING)
    private PickingStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setStatus(PickingStatus status) {
        this.status = status;
    }

    public void setPickedItems(List<Position> pickedItems) {
        this.pickedItems = pickedItems;
    }

    public PickingStatus getStatus() {
        return status;
    }

    @Override
    public List<Position> getPositions() {
        return pickedItems;
    }

    public String getId() {
        return id;
    }

    public enum PickingStatus {OPEN}
}
