package com.leyou.controller;

import com.leyou.Service.SpuService;
import com.leyou.common.PageResult;
import com.leyou.domain.Spu;
import com.leyou.domain.SpuDetail;
import com.leyou.domain.bo.SpuBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * 分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<SpuBo>> findSpuBoByPage(
       @RequestParam(value = "key", required = false) String key,
       @RequestParam(value = "saleable", required = false) Boolean saleable,
       @RequestParam(value = "page", defaultValue = "1") Integer page,
       @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ){
        PageResult<SpuBo> result =this.spuService.findSpuBoByPage(key, saleable, page, rows);
        if (result == null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 通过spuId查询spudetail
     * @return
     */
    @GetMapping("detail/{spuId}")
    public ResponseEntity<SpuDetail> findSpuDetailBySpuId(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail =  this.spuService.findSpuDetailBySpuId(spuId);
        if (spuDetail == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }




    @GetMapping("{spuid}")
    public ResponseEntity<Spu> findSpuById(@PathVariable("spuid") Long spuId){
        Spu spu =  this.spuService.findSpuById(spuId);
        if (spu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }
}
