package com.leyou.page.service.impl;

import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.clients.ItemClients;
import com.leyou.item.dto.*;
import com.leyou.page.dto.SpecGroupNameDTO;
import com.leyou.page.dto.SpecParamNameDTO;
import com.leyou.page.service.GoodsPageService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class GoodsPageServiceImpl implements GoodsPageService {

    private final ItemClients itemClient;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX_SPU = "page:spu:id:";
    private static final String KEY_PREFIX_SKU = "page:sku:id:";
    private static final String KEY_PREFIX_DETAIL = "page:detail:id:";
    private static final String KEY_PREFIX_CATEGORY = "page:category:id:";
    private static final String KEY_PREFIX_BRAND = "page:brand:id:";
    private static final String KEY_PREFIX_SPEC = "page:spec:id:";

    public GoodsPageServiceImpl(ItemClients itemClient, StringRedisTemplate redisTemplate) {
        this.itemClient = itemClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String loadSpuData(Long spuId) {
        // 查询信息
        SpuDTO spu = itemClient.querySpuById(spuId);
        // 组织数据
        Map<String, Object> map = new HashMap<>();
        map.put("id", spu.getId());
        map.put("name", spu.getName());
        map.put("categoryIds", spu.getCategoryIds());
        map.put("brandId", spu.getBrandId());
        String json = JsonUtils.toJson(map);
        // 存入redis, 如果数据量逐渐增多，可以用SSDB代替
        redisTemplate.opsForValue().set(KEY_PREFIX_SPU + spuId, json);
        return json;
    }

    @Override
    public String loadSpuDetailData(Long spuId) {
        // 查询信息
        SpuDetailDTO detail = itemClient.queryDetailById(spuId);
        String json = JsonUtils.toJson(detail);
        // 存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_DETAIL + spuId, json);
        return json;
    }

    @Override
    public String loadSkuListData(Long spuId) {
        // 查询信息
        List<SkuDTO> skuList = itemClient.querySkuBySpuId(spuId);
        String json = JsonUtils.toJson(skuList);
        // 存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_SKU + spuId, json);
        return json;
    }

    @Override
    public String loadCategoriesData(List<Long> ids) {
        // 查询信息
        List<CategoryDTO> list = itemClient.queryCategoryByIds(ids);

        List<Map<String, Object>> categoryList = list.stream().map(categoryDTO -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", categoryDTO.getId());
            map.put("name", categoryDTO.getName());
            return map;
        }).collect(Collectors.toList());
        String json = JsonUtils.toJson(categoryList);
        // 存入Redis
        redisTemplate.opsForValue().set(KEY_PREFIX_CATEGORY + ids.get(2), json);
        return json;
    }

    @Override
    public String loadBrandData(Long brandId) {
        // 查询信息
        BrandDTO brand = itemClient.queryBrandById(brandId);
        Map<String, Object> map = new HashMap<>();
        map.put("id", brand.getId());
        map.put("name", brand.getName());
        String json = JsonUtils.toJson(map);
        // 存入Redis
        redisTemplate.opsForValue().set(KEY_PREFIX_BRAND + brandId, json);
        return json;
    }

    @Override
    public String loadSpecData(Long categoryId) {
        // 查询信息
        List<SpecGroupDTO> list = itemClient.querySpecList(categoryId);

        List<SpecGroupNameDTO> groupList = new ArrayList<>();
        for (SpecGroupDTO groupDTO : list) {
            SpecGroupNameDTO nameDTO = new SpecGroupNameDTO();
            nameDTO.setName(groupDTO.getName());

            nameDTO.setParams(BeanHelper.copyWithCollection(groupDTO.getParams(), SpecParamNameDTO.class));
            groupList.add(nameDTO);
        }

        String json = JsonUtils.toJson(groupList);
        // 存入Redis
        redisTemplate.opsForValue().set(KEY_PREFIX_SPEC + categoryId, json);
        return json;
    }

    @Override
    public void deleteSku(Long spuId){
        redisTemplate.delete(KEY_PREFIX_SKU + spuId);
    }
}