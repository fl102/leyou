package com.leyou.Service.impl;

import com.leyou.Dao.CategoryDao;
import com.leyou.Service.CategoryService;
import com.leyou.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public List<Category> findCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return categoryDao.select(category);
    }

    @Override
    public List<Category> findCategoryIdsByBid(Long bid) {
        return categoryDao.findCategoryIdsByBid(bid);
    }

    @Override
    public List<String> findCategoriesByCids(List<Long> cids) {
        List<Category> categories = categoryDao.selectByIdList(cids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
