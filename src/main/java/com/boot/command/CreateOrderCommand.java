package com.boot.command;

import com.boot.order.dto.OrderEntryDTO;
import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class CreateOrderCommand {
    private UUID orderId;
    private String email;
    private long userId;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipPostalCode;
    private String country;
    private List<OrderEntryDTO> entries;

    @Override
    @TargetAggregateIdentifier
    public String toString() {
        return this.orderId + "-order";
    }
}
