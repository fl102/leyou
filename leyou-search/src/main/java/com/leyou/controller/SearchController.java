package com.leyou.controller;

import com.leyou.common.PageResult;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.service.SearchService;
import org.apache.lucene.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> searchResult(@RequestBody SearchRequest searchRequest){
        SearchResult searchResult =  this.searchService.searchResult(searchRequest);
        if (searchResult == null && CollectionUtils.isEmpty(searchResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(searchResult);
    }
}
