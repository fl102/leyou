package com.leyou.api;

import com.leyou.domain.Sku;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 通过spuid查询sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> findSkusBySpuId(@RequestParam("id") Long spuId);


    @GetMapping("sku/{skuId}")
    Sku findSkuBySkuId(@PathVariable("skuId") Long skuid);
}
