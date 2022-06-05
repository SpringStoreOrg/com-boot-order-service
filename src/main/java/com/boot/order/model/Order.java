package com.boot.order.model;

import com.boot.order.dto.OrderDTO;
import com.boot.order.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Accessors(chain = true)
@Entity
@Table(name = "orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Column
	private String addressLine1;

	@Column
	private String addressLine2;

	@Column
	private String city;

	@Column
	private String state;

	@Column
	private String zipPostalCode;

	@Column
	private String country;

	@Column(unique = true)
	private UUID uuid;
	
	@Column
	private OrderStatus status;

	@Column
	private double total;

	@Column
	private LocalDateTime lastUpdatedOn;

	@Column
	private String email;

	@JsonManagedReference
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY,  cascade = { CascadeType.ALL} )
	private List<OrderEntry> entries;

	@PreUpdate
	protected void lastUpdatedOnPreUpdate() {
		this.lastUpdatedOn =  LocalDateTime.now();
	}

	public static OrderDTO orderEntityToDto(Order order) {
		return new OrderDTO()
				.setUuid(order.getUuid())
				.setFirstName((order.getFirstName()))
				.setLastName((order.getLastName()))
				.setAddressLine1((order.getAddressLine1()))
				.setAddressLine2((order.getAddressLine2()))
				.setCity((order.getCity()))
				.setState((order.getState()))
				.setZipPostalCode((order.getZipPostalCode()))
				.setCountry((order.getCountry()))
				.setEntries(order.getEntries());
	}

	public static Order dtoToOrderEntity(OrderDTO orderDto) {
		return new Order()
				.setUuid(orderDto.getUuid())
				.setFirstName((orderDto.getFirstName()))
				.setLastName((orderDto.getLastName()))
				.setAddressLine1((orderDto.getAddressLine1()))
				.setAddressLine2((orderDto.getAddressLine2()))
				.setCity((orderDto.getCity()))
				.setState((orderDto.getState()))
				.setZipPostalCode((orderDto.getZipPostalCode()))
				.setCountry((orderDto.getCountry()));

	}
}
