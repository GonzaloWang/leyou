package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SpuDetailService detailService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SpecParamService specParamService;

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
    @GetMapping("/spu/page")
    public ResponseEntity<PageDTO<SpuDTO>> queryGoodsByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "id", required = false) Long id
    ) {
        return ResponseEntity.ok(spuService.queryGoodsByPage(page, rows, saleable, categoryId, brandId, id));
    }

    /**
     * 新增商品
     *
     * @param spuDTO 页面提交商品信息
     * @return 无
     */
    @Transactional
    @PostMapping("/spu")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO) {
        spuService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 商品上下架
     * @param id  id
     * @param saleable saleable
     * @return ResponseEntity<Void>
     */
    @PutMapping("/saleable")
    public ResponseEntity<Void> updateSpuSaleable(@RequestParam("id") Long id, @RequestParam("saleable") Boolean saleable) {
        spuService.updateSaleable(id, saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据id查询商品
     *
     * @param id 商品id
     * @return 商品信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpuDTO> queryGoodsById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(spuService.queryGoodsById(id));
    }

    /**
     * 商品修改
     */
    @PutMapping("/spu")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {
        spuService.updateGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 根据id查询SpuDetail
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> queryDetailById(@RequestParam("id")Long id) {
        return ResponseEntity.ok(detailService.queryDetailById(id));
    }


    /**1
     * 根据SpuId查询SKU集合
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("id")Long id) {

        return ResponseEntity.ok(SkuDTO.convertEntityList(skuService.query().eq("spu_id",id).list()));
    }

    /**
     * 根据id集合查询SKU集合
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(
                SkuDTO.convertEntityList(skuService.listByIds(ids))
        );
    }


    /**
     * 查询商品规格参数键值对
     */

    @GetMapping("/spec/value")
    public ResponseEntity<List<SpecParamDTO>> querySpecsValues(
            @RequestParam("id") Long id,
            @RequestParam(value = "searching",required = false) Boolean searching
    ) {
        return ResponseEntity.ok(specParamService.querySpecsValues(id,searching));
    }

    /**
     * 根据Id查询商品Spu
     */

    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(new SpuDTO(spuService.getById(id)));
    }
}