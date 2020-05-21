package com.leyou.Dao;

import com.leyou.domain.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandDao extends Mapper<Brand> {
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid}) ")
    void saveCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long id);

    @Select("select * from tb_brand a inner join tb_category_brand b on a.id = b.brand_id where category_id = #{cid}")
    List<Brand> findBrandsByCid(Long cid);
}
