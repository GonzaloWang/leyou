package com.leyou.item.clients;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface ItemClients {

    /**
     * 分页查询spu
     *
     * @param page       当前页
     * @param rows       每页大小
     * @param saleable   上架商品或下降商品
     * @param brandId    品牌id
     * @param categoryId 分类id
     * @param id         spu的id
     * @return 当前页商品数据
     */
    @GetMapping("/goods/spu/page")
    PageDTO<SpuDTO> queryGoodsByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "id", required = false) Long id
    );

    /**
     * 根据SpuId查询SKU集合
     * @param id
     * @return List<SkuDTO>
     */
    @GetMapping("/goods/sku/of/spu")
    List<SkuDTO> querySkuBySpuId(@RequestParam("id")Long id);

    /**
     * 查询商品规格参数键值对
     */

    @GetMapping("/goods/spec/value")
    List<SpecParamDTO> querySpecsValues(
            @RequestParam("id") Long id,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 根据品牌id集合查询品牌集合
     * @param ids 品牌id集合
     * @return ResponseEntity<List < BrandDTO>>
     */
    @GetMapping("/brand/list")
    List<BrandDTO> queryBrands(@RequestParam("ids") List<Long> ids);

    /**
     * 根据分类id的集合，查询商品分类的集合
     * @param ids  分类id的集合
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @GetMapping("/category/list")
    List<CategoryDTO> queryCategoryByIds(@RequestParam("ids")List<Long> ids);

    /**
     * 根据Id查询商品Spu
     */
    @GetMapping("/goods/spu/{id}")
    SpuDTO querySpuById(
            @PathVariable("id") Long id
    );

    /**
     * 根据id查询SpuDetail
     */
    @GetMapping("/goods/spu/detail")
    SpuDetailDTO queryDetailById(@RequestParam("id")Long id);

    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return  ResponseEntity<BrandDTO>
     */
    @GetMapping("/brand/{id}")
    BrandDTO queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据categoryid拆线呢组及组对应参数
     * @param cid
     * @return
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> querySpecList(@RequestParam("id")Long cid);
}
