package com.boot.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntryDTO {
    @Size(min = 3, max = 100)
    @NotNull
    private String productSlug;

    @Size(min = 3, max = 100)
    @NotNull
    private String productName;

    @Min(1)
    @NotNull
    private Double price;

    @Min(1)
    @NotNull
    private Integer quantity;
}
