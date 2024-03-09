package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderGetDetailsDTO{
    private UUID uuid;
    private double total;
    private double productTotal;
    private double shippingCost;
    private String state;
    private Integer productCount;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
    private String courier;
    private String trackingNumber;
    private String trackingUrl;
    private LocalDate deliveryDate;

    private List<OrderEntryGetDTO> entries;
    private AddressDTO shippingAddress;
    private AddressDTO receiptAddress;
    private String notes;
}
