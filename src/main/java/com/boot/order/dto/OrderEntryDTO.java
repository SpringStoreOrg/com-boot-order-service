package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntryDTO {
    private String productName;
    private double price;
    private Integer quantity;
}
