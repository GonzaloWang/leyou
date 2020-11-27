package com.leyou.search.service;

import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.starter.elastic.entity.PageInfo;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Mono<List<String>> getSuggest(String key);

    Mono<PageInfo<Goods>> listData(SearchRequest searchRequest);

    Mono<Map<String, List<?>>> listFilter(SearchRequest searchRequest);
}
