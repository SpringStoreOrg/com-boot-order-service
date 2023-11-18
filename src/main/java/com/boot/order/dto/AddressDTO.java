package com.boot.order.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {
    @Size(min = 3, message = "Min First name size is 3 characters!")
    @Size(max = 30, message = "Max First name size is 30 characters!")
    @NotNull
    private String firstName;
    @Size(min = 3, message = "Min Last name size is 3 characters!")
    @Size(max = 30, message = "Max Last name size is 30 characters!")
    @NotNull
    private String lastName;
    @Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
    @NotNull
    private String phoneNumber;
    @Email(message = "Invalid Email!")
    @NotNull
    private String email;
    @Size(min = 4, message = "Min Country size is 4 characters!")
    @Size(max = 60, message = "Max Country size is 60 characters!")
    @NotNull
    private String country;
    @Size(min = 3, message = "Min County size is 3 characters!")
    @Size(max = 20, message = "Max County size is 30 characters!")
    @NotNull
    private String county;
    @Size(min = 2, message = "Min City size is 2 characters!")
    @Size(max = 30, message = "Max City size is 30 characters!")
    @NotNull
    private String city;
    @Pattern(regexp="\\d{6}",message="Invalid Postal code")
    @NotNull
    private String postalCode;
    @Size(min = 3, message = "Min Street size is 3 characters!")
    @Size(max = 100, message = "Max Street size is 30 characters!")
    @NotNull
    private String street;
}
