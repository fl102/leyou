package com.leyou.Dao;


import com.leyou.domain.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryDao extends Mapper<Category>, SelectByIdListMapper<Category, Long>{

    /**
     * 根据品牌id查询商品分类
     * @param bid
     * @return
     */
    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> findCategoryIdsByBid(Long bid);
}
