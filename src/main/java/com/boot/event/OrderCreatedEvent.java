package com.boot.event;

import com.boot.order.dto.OrderEntryDTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class OrderCreatedEvent {
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
