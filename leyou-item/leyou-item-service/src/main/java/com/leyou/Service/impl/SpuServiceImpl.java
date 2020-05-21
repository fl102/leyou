package com.leyou.Service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.Dao.BrandDao;
import com.leyou.Dao.SpuDao;
import com.leyou.Dao.SpuDetailDao;
import com.leyou.Service.CategoryService;
import com.leyou.Service.SpuService;
import com.leyou.common.PageResult;
import com.leyou.domain.Brand;
import com.leyou.domain.Category;
import com.leyou.domain.Spu;
import com.leyou.domain.SpuDetail;
import com.leyou.domain.bo.SpuBo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpuServiceImpl implements SpuService{

    @Autowired
    private SpuDao spuDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuDetailDao spuDetailDao;

    /**
     * 分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuBo> findSpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加搜索条件
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title", "%" + key + "%");
        }
        //添加过滤条件
        if (saleable != null){
            criteria.andEqualTo("saleable", saleable);
        }
        //添加分页
         PageHelper.startPage(page, rows);
        //查询结果spu
        List<Spu> spus = this.spuDao.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        //spu结果集转变为spuBo结果集
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            Brand brand = brandDao.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            List<String> names = categoryService.findCategoriesByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            String cname = StringUtils.join(names, "-");
            spuBo.setCname(cname);
            return spuBo;
        }).collect(Collectors.toList());
        //封装成分页结果集

        return new PageResult<>(spuPageInfo.getTotal(), spuBos);
    }


    /**
     * 通过spuId查询spudetail
     * @return
     */
    @Override
    public SpuDetail findSpuDetailBySpuId(Long spuId) {
        return spuDetailDao.selectByPrimaryKey(spuId);
    }

    @Override
    public Spu findSpuById(Long spuId) {
        return this.spuDao.selectByPrimaryKey(spuId);
    }
}
