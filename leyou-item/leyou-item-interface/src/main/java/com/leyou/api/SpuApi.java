package com.leyou.api;

import com.leyou.common.PageResult;
import com.leyou.domain.Spu;
import com.leyou.domain.SpuDetail;
import com.leyou.domain.bo.SpuBo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("spu")
public interface SpuApi {
    /**
     * 通过spuId查询spudetail
     * @return
     */
    @GetMapping("detail/{spuId}")
    SpuDetail findSpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("page")
    PageResult<SpuBo> findSpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );

    @GetMapping("{spuid}")
    Spu findSpuById(@PathVariable("spuid") Long spuId);
}
