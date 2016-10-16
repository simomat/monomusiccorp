package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
}
