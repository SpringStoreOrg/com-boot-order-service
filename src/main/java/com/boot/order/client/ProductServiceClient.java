package com.boot.order.client;

import com.boot.order.dto.PagedProductsResponseDTO;
import com.boot.order.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="product-service")
public interface ProductServiceClient {
    @PostMapping(value = "/stock/reserve")
    @ResponseBody
    ResponseEntity<List<StockDTO>> reserveProducts(@RequestBody List<StockDTO> orders);

    @PostMapping(value = "/stock/release")
    @ResponseBody
    ResponseEntity<List<StockDTO>> releaseProducts(@RequestBody List<StockDTO> orders);

    @GetMapping
    @ResponseBody
    PagedProductsResponseDTO callGetAllProductsFromUserFavorites(@RequestParam("productNames") String productNames, @RequestParam("includeInactive") Boolean includeInactive);
}
