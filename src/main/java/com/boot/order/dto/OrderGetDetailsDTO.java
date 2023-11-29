package com.boot.order.dto;

import com.boot.order.enums.OrderState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    private String state;
    private Integer productCount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedOn;
    private String courier;
    private String trackingNumber;
    private String trackingUrl;

    private List<OrderEntryDTO> entries;
    private AddressDTO shippingAddress;
    private AddressDTO receiptAddress;
    private String notes;
}
