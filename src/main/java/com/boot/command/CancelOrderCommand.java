package com.boot.command;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
public class CancelOrderCommand {
    @TargetAggregateIdentifier
    private UUID orderId;
    private String email;
    private long userId;
    private String rejectionReason;
}
