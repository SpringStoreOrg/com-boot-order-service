package com.boot.command;

import com.boot.order.dto.OrderEntryDTO;
import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ReserveProductsCommand{
    private UUID orderId;
    private String email;
    private long userId;
    private List<OrderEntryDTO> entries;

    @Override
    @TargetAggregateIdentifier
    public String toString() {
        return this.orderId + "-product";
    }
}
