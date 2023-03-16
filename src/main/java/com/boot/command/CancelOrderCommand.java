package com.boot.command;

import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Builder
@Getter
public class CancelOrderCommand {
    private UUID orderId;
    private String email;
    private long userId;
    private String rejectionReason;

    @Override
    @TargetAggregateIdentifier
    public String toString() {
        return orderId + "-order";
    }
}
