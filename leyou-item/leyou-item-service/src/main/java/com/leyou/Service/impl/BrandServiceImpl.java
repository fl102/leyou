package com.leyou.Service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.Dao.BrandDao;
import com.leyou.Service.BrandService;
import com.leyou.common.PageResult;
import com.leyou.domain.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;
    @Override
    public PageResult<Brand> findBrandByPage(String key, Integer page, Integer rows, String sortBy, boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据Name模糊查询，或者根据首字母查询
        if (!StringUtils.isEmpty(key)){
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        //添加分页条件
        PageHelper.startPage(page, rows);
        //添加排序条件
        if (!StringUtils.isEmpty(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        List<Brand> brands = brandDao.selectByExample(example);

        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        PageResult<Brand> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }

    @Override
    @Transactional
    public void save(Brand brand, List<Long> cids) {
        this.brandDao.insertSelective(brand);
        cids.forEach(cid ->{
            this.brandDao.saveCategoryAndBrand(cid, brand.getId());
        });
    }

    @Override
    public List<Brand> findBrandsByCid(Long cid) {
        return this.brandDao.findBrandsByCid(cid);
    }

    @Override
    public Brand findBrandByid(Long id) {
        return this.brandDao.selectByPrimaryKey(id);
    }
}
