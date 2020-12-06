package com.jetbrains.invoice.controllers;

import com.jetbrains.invoice.entity.Invoice;
import com.jetbrains.invoice.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class InvoiceController {

    // we can consider adding @service, but since logic is quite simple at this moment, I kept it repository bounded to
    // controller directly, service would make code unnecessarily bloated

    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/invoices")
    public List<Invoice> getInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices;

    }

    @PostMapping("/add")
    @ResponseStatus
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice) {
        // what should we do in case of insufficient data inside json or unclear mapping of data
        // create fields mandatory using annotation inside Invoice entity
        if (invoice.getId() == null
                && invoiceRepository.findInvoiceBySalesId(invoice.getSalesSystemId()) == null) {
            Invoice createdInvoice = invoiceRepository.save(invoice);
            return createFormattedResponse(HttpStatus.CREATED, "invoice created with id: " + createdInvoice.getId());
        } else {
            return createFormattedResponse(HttpStatus.CONFLICT,
                    "please don't use /add endpoint with existing internal ID or sales system id \n" +
                            "use /update endpoint for updating");
        }
    }

    @PutMapping("/update/{id}")
    @ResponseStatus
    public ResponseEntity<?> updateInvoice(@PathVariable long id, @RequestBody Invoice invoice) {
        Optional<Invoice> invoiceExisting = invoiceRepository.findById(id);
        if (invoiceExisting.isPresent()) {
            invoice.setId(invoiceExisting.get().getId());
            invoiceRepository.save(invoice);
            return createFormattedResponse(HttpStatus.OK, "resource updated " + id);
        } else {
            return ResponseEntity.status((HttpStatus.NOT_FOUND)).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus
    public ResponseEntity<?> deleteInvoice(@PathVariable long id) {
        // can add on some verification of deletion or just simple accepted response will do
        Optional<Invoice> invoiceOptional = invoiceRepository.findById(id);
        if (invoiceOptional.isPresent()) {
            invoiceRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/invoice/bysalesid/{id}")
    public Invoice getInvoiceBySalesId(@PathVariable long id) {
        Invoice invoice = invoiceRepository.findInvoiceBySalesId(id);
        return invoice;
    }

    private ResponseEntity<?> createFormattedResponse(HttpStatus httpStatus, String shortDescription) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", httpStatus.toString());
        map.put("time", LocalDateTime.now());
        map.put("description", shortDescription);
        return ResponseEntity.status(httpStatus).body(map);
    }
}
