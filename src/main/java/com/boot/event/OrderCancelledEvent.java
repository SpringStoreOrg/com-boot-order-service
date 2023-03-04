package com.boot.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    private UUID orderId;
    private String email;
    private long userId;
    private String rejectionReason;
}
