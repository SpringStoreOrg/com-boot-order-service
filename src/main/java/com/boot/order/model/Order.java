package com.boot.order.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Accessors(chain = true)
@Entity
@Table(name = "order")
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
	@Type(type = "uuid-char")
	private UUID uuid;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Enumerated(EnumType.STRING)
	private RejectionReason rejectionReason;

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

	public void approve() {
		this.status = OrderStatus.IN_PROGRESS;
	}

	public void reject(RejectionReason rejectionReason) {
		this.status = OrderStatus.REJECTED;
		this.rejectionReason = rejectionReason;
	}
}
