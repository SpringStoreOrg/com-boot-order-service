package com.boot.event;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class OrderCompletedEvent {
    private UUID orderId;
    private String email;
    private long userId;
}
