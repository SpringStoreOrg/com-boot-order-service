package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSendDTO {
    @NotNull
    @Size(min = 3, max = 30)
    private String courier;

    @NotNull
    @Size(min = 3, max = 30)
    private String trackingNumber;

    @NotNull
    @Size(min = 10, max = 100)
    private String trackingUrl;

    @NotNull
    private LocalDate deliveryDate;
}
