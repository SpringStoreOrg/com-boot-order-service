package com.boot.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CartEntryDTO {

    private Long id;

    private String productName;

    private double price;

    private Integer quantity;

}
