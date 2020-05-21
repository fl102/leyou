package com.leyou.client;

import com.leyou.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", contextId = "SpecClient")
public interface SpecClient extends SpecApi {
}
