package com.jetbrains.invoice.controllers;

import com.jetbrains.invoice.entity.Invoice;
import com.jetbrains.invoice.repository.InvoiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/invoices")
    public List<Invoice> getInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices;

    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        Invoice invoiceCreated = invoiceRepository.save(invoice);
        return invoiceCreated;
    }
}
