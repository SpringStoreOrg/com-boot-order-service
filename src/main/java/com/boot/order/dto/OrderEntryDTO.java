package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntryDTO {
    @Size(min = 3, max = 100)
    private String productSlug;
    @Min(1)
    private double price;
    @Min(1)
    private Integer quantity;
}
