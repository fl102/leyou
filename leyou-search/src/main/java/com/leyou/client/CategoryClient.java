package com.leyou.client;

import com.leyou.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(value = "item-service", contextId = "CategoryClient")
public interface CategoryClient extends CategoryApi {
}
