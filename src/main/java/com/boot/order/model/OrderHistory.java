package com.boot.order.model;

import com.boot.order.enums.OrderState;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_history")
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Column
    private double total;

    @Column
    private Integer productCount;

    @Column
    private Long userId;

    @Column
    private LocalDateTime createdOn;

    @Column
    private String trackingNumber;

    @Column
    private LocalDate deliveryDate;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @PrePersist
    public void create(){
        this.createdOn = LocalDateTime.now();
    }
}
