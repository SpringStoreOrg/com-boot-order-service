package com.boot.order.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@DiscriminatorValue("person")
public class OrderPersonAddress extends OrderAddress{
    @Column
    private String firstName;
    @Column
    private String lastName;

    @Override
    public String getRecipient() {
        return firstName + " " + lastName;
    }
}
