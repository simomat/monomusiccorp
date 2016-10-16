package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.Invoice;

public interface InvoiceDelivery {
    void deliver(Invoice invoice);
}
