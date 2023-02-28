package com.boot.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

  private Long userId;
  private String userEmail;
  private OrderDTO orderDTO;
}
