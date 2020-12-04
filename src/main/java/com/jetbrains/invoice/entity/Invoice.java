package com.jetbrains.invoice.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue
    private Long id;

    // assume this will link to customer so it cannot be empty,
    // what use would be an invoice if we cannot charge anyone :)
    // possibly even validated if customer exists
    @NotNull
    private int customerInternalIdentifier;

    private String customerName;

    private String customerAddress;

    private Date orderDate;

    private Currency isoCurrency;

    private long orderReferenceNumber;

    private long productSKU;

    private String productName;

    @NotNull
    private int productQuantity;

    @NotNull
    private float price;

    @Column(unique = true)
    private long salesSystemId;

    @CreationTimestamp
    private Date dateOfUpload;

    private Date invoiceCreationDate;

    private boolean isPaid;

    private Date paidDate;

    // additional fields could be considered according to specific future needs
    // - additional dateUpdated
    // - payment due date
    // - boolean - has invoice been paid
    // - might consider splitting entity/table
}
