package com.leyou.service.impl;

import com.leyou.domain.*;
import com.leyou.goods.client.*;
import com.leyou.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsServiceImpl implements GoodService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpuClient spuClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private GoodsClient goodsClient;


    @Override
    public Map<String, Object> loadData(Long spuId) {
        //声明返回结果集
        Map<String, Object> map = new HashMap<>();
        //查询spu
        Spu spu = this.spuClient.findSpuById(spuId);
        map.put("spu", spu);
        if (spu != null){
            //查询分类 map<分类id, 分类名称>
            List<Map<String, Object>> categories = new ArrayList<>();
            List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<String> names = this.categoryClient.findCategoriesByCids(ids);
            for (int i = 0; i < names.size(); i++) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", ids.get(i));
                categoryMap.put("name", names.get(i));
                categories.add(categoryMap);
            }
            map.put("categories", categories);
            //查询品牌
            Brand brand = this.brandClient.findBrandById(spu.getBrandId());
            map.put("brand", brand);
            //查询skus
            List<Sku> skus = this.goodsClient.findSkusBySpuId(spu.getId());
            map.put("skus", skus);
            //查询spuDetail
            SpuDetail spuDetail = this.spuClient.findSpuDetailBySpuId(spu.getId());
            map.put("spuDetail", spuDetail);
            //查询规格参数组
            List<SpecGroup> groups = this.specClient.findGroupAndParamsByCid(spu.getCid3());
            map.put("groups", groups);
            //查询特殊规格参数
            List<SpecParam> params = this.specClient.findParams(null, spu.getCid3(), false, null);
            Map<Long, String> specParamMap = new HashMap<>();
            params.forEach(param -> {
                specParamMap.put(param.getId(), param.getName());
            });
            map.put("specParamMap", specParamMap);
        }
        return map;
    }
}
