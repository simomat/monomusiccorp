package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyInvoiceDeliveryImpl implements InvoiceDelivery {

    private final Logger logger = LoggerFactory.getLogger(DummyInvoiceDeliveryImpl.class);

    @Override
    public void deliver(Invoice invoice) {
        logger.info("delivering invoice to customer {} for {} ", invoice.getCustomer(), invoice.getPositions());
    }
}
