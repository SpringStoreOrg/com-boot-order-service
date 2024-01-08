package com.boot.order.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@DiscriminatorValue("company")
public class OrderCompanyAddress extends OrderAddress{
    @Column
    private String companyName;
    @Column
    private String identificationNumber;
    @Column
    private String registrationNumber;
}
