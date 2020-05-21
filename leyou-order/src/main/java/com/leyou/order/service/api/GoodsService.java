package com.leyou.order.service.api;


import com.leyou.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface GoodsService extends GoodsApi {
}
