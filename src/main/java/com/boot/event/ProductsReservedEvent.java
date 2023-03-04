package com.boot.event;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductsReservedEvent {
    private UUID orderId;
    private String email;
    private long userId;
}
