package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
	private UUID uuid;
	@NotNull
	@NotEmpty
	private List<OrderEntryDTO> entries;
	@NotNull
	private AddressDTO shippingAddress;
	@NotNull
	private AddressDTO receiptAddress;
	@Size(max = 400)
	private String notes;
}
