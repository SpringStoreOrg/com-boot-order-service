package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

	private List<OrderEntryDTO> entries;
}
