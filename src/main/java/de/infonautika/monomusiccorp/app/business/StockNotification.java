package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.PickingOrder;

public interface StockNotification {
    void newPickingOrder(PickingOrder pickingOrder);
}
