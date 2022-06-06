package com.boot.order.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@Table(name = "order_entry")
public class OrderEntry  implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@Column
	private String productName;

	@Column
	private Integer quantity;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
}
