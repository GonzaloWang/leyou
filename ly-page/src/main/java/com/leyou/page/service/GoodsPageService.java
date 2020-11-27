package com.leyou.page.service;

import java.util.List;


public interface GoodsPageService {
    /**
     * 加载spu到redis中并返回
     * @param spuId 商品id
     */
    String loadSpuData(Long spuId);

    /**
     * 加载spuDetail到redis中并返回
     * @param spuId 商品id
     */
    String loadSpuDetailData(Long spuId);

    /**
     * 加载sku信息到redis中并返回
     * @param spuId 商品id
     */
    String loadSkuListData(Long spuId);

    /**
     * 加载分类信息到redis中并返回
     * @param ids 商品分类的三级分类id
     */
    String loadCategoriesData(List<Long> ids);

    /**
     * 加载品牌信息到redis中并返回
     * @param brandId 品牌id
     */
    String loadBrandData(Long brandId);

    /**
     * 加载规格参数信息到redis中并返回
     * @param categoryId 商品第三级分类id
     */
    String loadSpecData(Long categoryId);

    /**
     * 把Sku从Redis删除
     */
    void deleteSku(Long spuId);
}