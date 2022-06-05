package com.boot.order.dto;

import com.boot.order.model.OrderEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

	private UUID uuid;

	private String firstName;

	private String lastName;

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private String zipPostalCode;

	private String country;

	private List<OrderEntry> entries;
}
