package com.leyou.Service;

import com.leyou.common.PageResult;
import com.leyou.domain.Spu;
import com.leyou.domain.SpuDetail;
import com.leyou.domain.bo.SpuBo;

public interface SpuService {
    PageResult<SpuBo> findSpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    SpuDetail findSpuDetailBySpuId(Long spuId);

    Spu findSpuById(Long spuId);
}
