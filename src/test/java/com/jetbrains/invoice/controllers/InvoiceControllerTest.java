package com.jetbrains.invoice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.invoice.entity.Invoice;
import com.jetbrains.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceRepository invoiceRepository;

    private static final String SAMPLE_JSON = "{\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10T00:00:00.000+00:00\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2},";

    private static final String WRONG_JSON = "{\"id\":1,\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10T00:00:00.000+00:00\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2},";

    @Test
    public void shouldReturnJsonMediaType() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        Mockito.when(invoiceRepository.findAll()).thenReturn(Arrays.asList(invoice));
        this.mockMvc.perform(get("/invoices")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldCallRepoTwoTimesAndRemoveInvoice() throws Exception {
        Long id = 1L;
        Invoice invoice = new Invoice();
        invoice.setId(id);
        Mockito.when(invoiceRepository.findById(id)).thenReturn(Optional.of(invoice));
        Mockito.doNothing().when(invoiceRepository).deleteById(id);

        this.mockMvc.perform(delete("/delete/" + id)).andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void shouldRespondProperlyToNonExistingDeleteRequest() throws Exception {
        Long id = 1L;
        Long nonExistingId = 2L;
        Invoice invoice = new Invoice();
        invoice.setId(id);
        Mockito.when(invoiceRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.doNothing().when(invoiceRepository).deleteById(id);

        this.mockMvc.perform(delete("/delete/" + nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void addingToAlreadyExistingPosition() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = objectMapper.readValue(WRONG_JSON, Invoice.class);
        Mockito.when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        this.mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(WRONG_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testingCorrectInvoiceSave() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = objectMapper.readValue(SAMPLE_JSON, Invoice.class);
        Mockito.when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        Mockito.when(invoiceRepository.findInvoiceBySalesId(invoice.getSalesSystemId())).thenReturn(null);
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(invoice);

        this.mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(SAMPLE_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testingCorrectUpdate() throws Exception {
        Long id = 1L;
        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = objectMapper.readValue(SAMPLE_JSON, Invoice.class);
        Mockito.when(invoiceRepository.findById(id)).thenReturn(Optional.of(invoice));
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(invoice);

        this.mockMvc.perform(put("/update/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(SAMPLE_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
