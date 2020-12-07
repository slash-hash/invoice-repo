package com.jetbrains.invoice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.invoice.entity.Invoice;
import com.jetbrains.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private static final String SAMPLE_JSON_WITHOUT_ID = "{\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10T00:00:00.000+00:00\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2, \"salesSystemId\":203, \"invoiceCreationDate\": \"2014-05-30\"}";
    private static final String SAMPLE_JSON_WITH_ID_1 = "{\"id\":1,\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10T00:00:00.000+00:00\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2}";
    private static final String SAMPLE_JSON_INVOICE_ID_2 = "{\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2, \"salesSystemId\":203, \"invoiceCreationDate\": \"2014-05-30\", \"id\":2}";

    private Invoice testingInvoiceWithId1;
    private Invoice testingInvoiceWithOutId;

    @BeforeEach
    void init() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        testingInvoiceWithId1 = objectMapper.readValue(SAMPLE_JSON_WITH_ID_1, Invoice.class);
        testingInvoiceWithOutId = objectMapper.readValue(SAMPLE_JSON_WITHOUT_ID, Invoice.class);
    }


    @Test
    public void shouldReturnJsonMediaType() throws Exception {
        Mockito.when(invoiceRepository.findAll()).thenReturn(Arrays.asList(testingInvoiceWithId1));

        this.mockMvc.perform(get("/invoices")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void shouldCallRepoTwoTimesAndRemoveInvoice() throws Exception {
        Long id = 1L;
        Mockito.when(invoiceRepository.findById(id)).thenReturn(Optional.of(testingInvoiceWithId1));
        Mockito.doNothing().when(invoiceRepository).deleteById(id);

        this.mockMvc.perform(delete("/delete/" + id)).andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void shouldRespondProperlyToNonExistingDeleteRequest() throws Exception {
        Long nonExistingId = 2L;
        Mockito.when(invoiceRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/delete/" + nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void addingToAlreadyExistingPosition() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = objectMapper.readValue(SAMPLE_JSON_WITH_ID_1, Invoice.class);

        Mockito.when(invoiceRepository.findById(testingInvoiceWithId1.getId())).thenReturn(Optional.of(testingInvoiceWithId1));

        this.mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(SAMPLE_JSON_WITH_ID_1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testingCorrectInvoiceSave() throws Exception {
        Mockito.when(invoiceRepository.findInvoiceBySalesId(testingInvoiceWithOutId.getSalesSystemId())).thenReturn(null);
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(testingInvoiceWithOutId);

        this.mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(SAMPLE_JSON_WITHOUT_ID))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testingCorrectUpdate() throws Exception {
        Mockito.when(invoiceRepository.findById(testingInvoiceWithId1.getId())).thenReturn(Optional.of(testingInvoiceWithId1));
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(testingInvoiceWithId1);

        this.mockMvc.perform(put("/update/" + testingInvoiceWithId1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(SAMPLE_JSON_WITH_ID_1))
                .andExpect(status().isOk());
    }
}
