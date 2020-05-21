package com.leyou.Service;

import com.leyou.domain.Sku;
import com.leyou.domain.bo.SpuBo;

import java.util.List;

public interface GoodsService {
    void saveGoods(SpuBo spuBo);

    List<Sku> findSkusBySpuId(Long spuId);

    void UpDateGoods(SpuBo spuBo);

    Sku findSkuBySkuId(Long skuid);
}
