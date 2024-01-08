package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CompanyAddressDTO.class, name = "company"),
        @JsonSubTypes.Type(value = PersonAddressDTO.class, name = "person")
})
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
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
