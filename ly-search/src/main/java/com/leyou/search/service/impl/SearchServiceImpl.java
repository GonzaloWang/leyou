package com.leyou.search.service.impl;

import com.leyou.common.exception.LyException;
import com.leyou.item.clients.ItemClients;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.search.respository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.entity.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsRepository respository;

    @Autowired
    private ItemClients itemClients;

    @Override
    public Mono<List<String>> getSuggest(String key) {
        return respository.suggestBySingleField("suggestion", key);
    }

    @Override
    public Mono<PageInfo<Goods>> listData(SearchRequest searchRequest) {



        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = getQueryBuilder(searchRequest);
        // 添加查询条件
        sourceBuilder.query(queryBuilder);
        int size = searchRequest.getRows();
        // page-1*size
        Integer page = searchRequest.getPage() == null ? 1 : searchRequest.getPage();
        int from = (page - 1) * size;
        sourceBuilder.size(size);
        sourceBuilder.from(from);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<am>");
        highlightBuilder.postTags("</am>");
        highlightBuilder.field("title");
        sourceBuilder.highlighter(highlightBuilder);

        // 动态排序条件
        String sortBy = searchRequest.getSortBy();
        if (StringUtils.isNoneBlank(sortBy)) {
            sourceBuilder.sort(SortBuilders.fieldSort(sortBy).order(searchRequest.getDesc() ? SortOrder.DESC : SortOrder.ASC));
        }

        return this.respository.queryBySourceBuilderForPageHighlight(sourceBuilder);
    }

    @Override
    public Mono<Map<String, List<?>>> listFilter(SearchRequest searchRequest) {


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 添加查询条件
        sourceBuilder.query(getQueryBuilder(searchRequest));

        sourceBuilder.aggregation(AggregationBuilders.terms("brandAgg").field("brandId").size(20));
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryAgg").field("categoryId").size(20));

        // 过滤条件的聚合以及过滤条件值的聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("specAgg","specs")
                .subAggregation(AggregationBuilders.terms("nameAgg").field("specs.name")
                        .subAggregation(AggregationBuilders.terms("valueAgg").field("specs.value"))));

        Mono<Aggregations> aggregationsMono = this.respository.aggregationBySourceBuilder(sourceBuilder);

        return aggregationsMono.map(aggregations -> {
            // 过滤条件的封装集合
            Map<String, List<?>> resultMap = new LinkedHashMap<>();
            // 解析品牌聚合,获取到品牌id后,根据品牌id集合查询对应的品牌集合
            Terms brandAgg = aggregations.get("brandAgg");

            List<Long> brandIds = brandAgg
                    .getBuckets()
                    .stream()
                    .map(bucket -> ((Terms.Bucket) bucket).getKeyAsNumber())
                    .map(Number::longValue)
                    .collect(Collectors.toList());
            // 解析分类聚合,获取到分类id后,根据分类id集合查询对应的分类集合
            Terms categoryAgg = aggregations.get("categoryAgg");
            List<Long> categoryIds = categoryAgg
                    .getBuckets()
                    .stream()
                    .map(bucket -> ((Terms.Bucket) bucket).getKeyAsNumber())
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(categoryIds)) {
                resultMap.put("分类",this.itemClients.queryCategoryByIds(categoryIds));
            }
            if (!CollectionUtils.isEmpty(brandIds)) {
                resultMap.put("品牌",this.itemClients.queryBrands(brandIds));
            }
            // 解析规格参数的nested聚合,
            Nested specAgg = aggregations.get("specAgg");
            // 获取名称聚合
            Terms nameAgg = specAgg.getAggregations().get("nameAgg");
            // 遍历名称数组
            nameAgg.getBuckets().forEach(bucket->{
                // 获取每个名称值
                String key = ((Terms.Bucket) bucket).getKeyAsString();
                // 获取当前名称聚合的子聚合
                Terms valueAgg = ((Terms.Bucket) bucket).getAggregations().get("valueAgg");
                // 把名称对应的值封装成数组
                List<String> valueList = valueAgg.getBuckets().stream().map(valueBucket -> ((Terms.Bucket) valueBucket).getKeyAsString()).collect(Collectors.toList());

                resultMap.put(key, valueList);
            });


            return resultMap;
        });


    }

    private QueryBuilder getQueryBuilder(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isEmpty(key)) {
            throw new LyException(400, "请求参数有误");
        }
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 给查询构造条件添加条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", key).operator(Operator.AND));
        // 获取过滤条件,如果过滤条件不为空,则需要给查询构造条件,循环添加过滤条件
        Map<String, String> filters = searchRequest.getFilters();
        if (!CollectionUtils.isEmpty(filters)) {
            filters.entrySet().forEach(entry->{
                String eKey = entry.getKey();
                String eValue = entry.getValue();

                if ("分类".equals(eKey)) {
                    eKey = "categoryId";
                    boolQueryBuilder.filter(QueryBuilders.termQuery(eKey, eValue));
                } else if ("品牌".equals(eKey)) {
                    eKey="brandId";
                    boolQueryBuilder.filter(QueryBuilders.termQuery(eKey, eValue));
                }

            });
        }

        return boolQueryBuilder;
    }
}
