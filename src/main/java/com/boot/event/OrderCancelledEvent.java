package com.boot.event;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
public class OrderCancelledEvent {
    private UUID orderId;
    private String email;
    private long userId;
    private String rejectionReason;
}
