package com.leyou.controller;

import com.leyou.Service.CategoryService;
import com.leyou.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> findCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid){
        if (pid == null || pid.longValue() < 0){
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.findCategoriesByPid(pid);
         if (CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(categories);
    }


    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> findCategoryIdsByBid(@PathVariable(value = "bid") Long bid){
        if (bid == null || bid.longValue() < 0){
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.findCategoryIdsByBid(bid);
        if (categories == null || categories.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据商品分类ids查询商品分类
     * @param Cids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> findCategoriesByCids(@RequestParam("ids") List<Long> Cids){
        List<String> names = this.categoryService.findCategoriesByCids(Cids);
        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
