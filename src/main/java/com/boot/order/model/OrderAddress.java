package com.boot.order.model;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "order_address")
@DiscriminatorColumn(name = "type")
public class OrderAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String country;

    @Column
    private String county;

    @Column
    private String city;

    @Column
    private String postalCode;

    @Column
    private String street;

    @Column
    private LocalDateTime createdOn;

    public String getRecipient(){
        return StringUtils.EMPTY;
    }

    @PrePersist
    public void create(){
        this.createdOn = LocalDateTime.now();
    }
}
