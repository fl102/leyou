package com.leyou.Service;

import com.leyou.common.PageResult;
import com.leyou.domain.Brand;

import java.util.List;

public interface BrandService {
    PageResult<Brand> findBrandByPage(String key, Integer page, Integer rows, String sortBy, boolean desc);

    void save(Brand brand, List<Long> cids);

    List<Brand> findBrandsByCid(Long cid);

    Brand findBrandByid(Long id);
}
