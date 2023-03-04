package com.boot.command;

import com.boot.order.dto.OrderEntryDTO;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
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
}
