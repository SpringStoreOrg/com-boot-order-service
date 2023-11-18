package com.boot.order.client;

import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name="product-service")
public interface ProductServiceClient {
    @PostMapping(value = "/stock/reserve")
    ResponseEntity<List<StockDTO>> reserveProducts(List<StockDTO> orders);

    @PostMapping(value = "/stock/release")
    ResponseEntity<List<StockDTO>> releaseProducts(List<StockDTO> orders);
}
