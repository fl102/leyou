package com.leyou.Service;

import com.leyou.domain.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findCategoriesByPid(Long pid);

    List<Category> findCategoryIdsByBid(Long bid);

     List<String> findCategoriesByCids(List<Long> Cids);

}
