package com.boot.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductReservedEvent {
    private List<OrderEntryDTO> entries;
}
