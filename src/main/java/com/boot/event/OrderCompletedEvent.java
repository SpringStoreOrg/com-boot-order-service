package com.boot.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderCompletedEvent {
    private UUID orderId;
    private String email;
    private long userId;
}
