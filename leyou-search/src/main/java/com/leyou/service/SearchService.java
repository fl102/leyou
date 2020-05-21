package com.leyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.common.PageResult;
import com.leyou.domain.Spu;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;

public interface SearchService {
    Goods buildGoods(Spu spu) throws Exception;

    SearchResult searchResult(SearchRequest searchRequest);

    void createIndex(Long id) throws Exception;

    void deleteById(Long id);
}
