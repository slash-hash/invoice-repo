package com.jetbrains.invoice.repository;

import com.jetbrains.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "SELECT * FROM invoices i WHERE i.sales_system_id = ?1", nativeQuery = true)
    public Invoice findInvoiceBySalesId(long salesSystemId);
}
