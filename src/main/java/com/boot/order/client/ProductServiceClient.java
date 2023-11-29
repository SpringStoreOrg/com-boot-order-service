package com.boot.order.client;

import com.boot.order.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name="product-service")
public interface ProductServiceClient {
    @PostMapping(value = "/stock/reserve")
    @ResponseBody
    ResponseEntity<List<StockDTO>> reserveProducts(@RequestBody List<StockDTO> orders);

    @PostMapping(value = "/stock/release")
    @ResponseBody
    ResponseEntity<List<StockDTO>> releaseProducts(@RequestBody List<StockDTO> orders);
}
