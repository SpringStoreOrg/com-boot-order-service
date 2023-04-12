package com.boot.order.client;

import com.boot.order.dto.OrderEntryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name="product-service")
public interface ProductServiceClient {
    @PostMapping(value = "/stock/subtract")
    ResponseEntity subtractProducts(List<OrderEntryDTO> orders);

    @PostMapping(value = "/stock/add")
    ResponseEntity addProducts(List<OrderEntryDTO> orders);
}
