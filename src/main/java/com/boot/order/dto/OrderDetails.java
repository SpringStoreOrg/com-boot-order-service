package com.boot.order.dto;

import javax.persistence.Embeddable;

@Embeddable
public class OrderDetails {

  private Long customerId;

  public OrderDetails() {
  }

  public OrderDetails(Long customerId) {
    this.customerId = customerId;
  }

  public Long getCustomerId() {
    return customerId;
  }
}
