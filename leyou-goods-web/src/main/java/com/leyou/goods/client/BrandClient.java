package com.leyou.goods.client;

import com.leyou.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", contextId = "BrandClient")
public interface BrandClient extends BrandApi {
}
