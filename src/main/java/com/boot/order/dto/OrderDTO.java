package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
	@Valid
	@NotNull
	@NotEmpty
	private List<OrderEntryDTO> entries;
	@Valid
	@NotNull
	private AddressDTO shippingAddress;
	@Valid
	private AddressDTO receiptAddress;
	@Size(max = 400)
	private String notes;
}
