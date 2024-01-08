package com.boot.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CompanyAddressDTO extends AddressDTO{
    @Size(min = 4, message = "Min Company Name size is 4 characters!")
    @Size(max = 100, message = "Max Company Name size is 100 characters!")
    @NotNull
    private String companyName;
    @Size(min = 1, message = "Min Identification Number size is 1 characters!")
    @Size(max = 15, message = "Max Identification Number size is 15 characters!")
    @NotNull
    private String identificationNumber;
    @Size(min = 1, message = "Min Registration Number size is 1 characters!")
    @Size(max = 15, message = "Max Registration Number size is 15 characters!")
    @NotNull
    private String registrationNumber;
}
