package com.leyou.client;

import com.leyou.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", contextId = "GoodsClient")
public interface GoodsClient extends GoodsApi{

}
