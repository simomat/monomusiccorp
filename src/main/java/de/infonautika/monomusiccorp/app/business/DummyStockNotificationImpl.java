package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.PickingOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyStockNotificationImpl implements StockNotification {

    private final Logger logger = LoggerFactory.getLogger(DummyStockNotificationImpl.class);

    @Override
    public void newPickingOrder(PickingOrder pickingOrder) {
        logger.info("notification for new picking order {}", pickingOrder.getId());
    }
}
