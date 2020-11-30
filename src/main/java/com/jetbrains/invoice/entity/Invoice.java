package com.jetbrains.invoice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue
    private Long id;

    private String customerInternalIdentifier;

    private String customerName;

    private String customerAddress;

    private Date orderDate;

    private String isoCurrency;

    private long orderReferenceNumber;

    private String productSKU;

    private String productName;

    private long productQuantity;

    private double price;
}
