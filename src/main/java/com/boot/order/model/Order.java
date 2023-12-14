package com.boot.order.model;

import com.boot.order.enums.OrderState;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Entity
@Table(name = "order")
public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(unique = true)
	@Type(type = "uuid-char")
	private UUID uuid;

	@Column
	private Long userId;
	
	@Enumerated(EnumType.STRING)
	private OrderState state;

	@Column
	private double total;

	@Column
	private Integer productCount;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "receipt_address_id")
	private OrderAddress receiptAddress;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "shipping_address_id")
	private OrderAddress shippingAddress;

	@Column
	private LocalDateTime createdOn;

	@Column
	private String courier;

	@Column
	private String trackingNumber;

	@Column
	private String trackingUrl;

	@Column
	private LocalDate deliveryDate;

	@Column
	private String notes;

	@Column
	private LocalDateTime lastUpdatedOn;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY,  cascade = { CascadeType.ALL} )
	private List<OrderEntry> entries;

	@PrePersist
	public void create(){
		this.createdOn = LocalDateTime.now();
		this.lastUpdatedOn = LocalDateTime.now();
	}

	@PreUpdate
	protected void update() {
		this.lastUpdatedOn =  LocalDateTime.now();
	}
}
