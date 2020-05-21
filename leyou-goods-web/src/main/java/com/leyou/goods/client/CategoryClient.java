package com.leyou.goods.client;

import com.leyou.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", contextId = "CategoryClient")
public interface CategoryClient extends CategoryApi {
}
