package com.boot.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonAddressDTO extends AddressDTO{
    @Size(min = 3, message = "Min First name size is 3 characters!")
    @Size(max = 30, message = "Max First name size is 30 characters!")
    @NotNull
    private String firstName;
    @Size(min = 3, message = "Min Last name size is 3 characters!")
    @Size(max = 30, message = "Max Last name size is 30 characters!")
    @NotNull
    private String lastName;
}
