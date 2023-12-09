package com.boot.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntryGetDTO {
    private String productSlug;
    private String productName;
    private String productImg;
    private Double price;
    private Integer quantity;
}
