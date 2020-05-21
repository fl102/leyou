package com.leyou.goods.client;

import com.leyou.api.SpuApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", contextId = "SpuClient")
public interface SpuClient extends SpuApi {
}
