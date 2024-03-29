package com.boot.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderGetDTO {
    private UUID uuid;
    private double total;
    private String state;
    private Integer productCount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedOn;
    private LocalDate deliveryDate;
}
