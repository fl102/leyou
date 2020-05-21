package com.leyou.api;

import com.leyou.domain.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据品牌id查询品牌
     *  @param id
     * @return
     */
    @GetMapping("{id}")
    Brand findBrandById(@PathVariable("id") Long id);
}
