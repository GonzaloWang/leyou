package com.leyou.item.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据品牌id集合查询品牌集合
     *
     * @param ids 品牌id集合
     * @return ResponseEntity<List < BrandDTO>>
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> queryBrands(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(BrandDTO.convertEntityList(brandService.listByIds(ids)));
    }

    /**
     * 根据分类id查询品牌
     *
     * @param id 分类id
     * @return 品牌集合
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCategoryId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(brandService.queryBrandByCategoryId(id));
    }

    /**
     *根据关键字搜索品牌，并分页返回结果
     * @param page 当前页码
     * @param rows 每页大小
     * @param key 搜索条件
     * @return  ResponseEntity<PageDTO<BrandDTO>>
     */
    @GetMapping("/page")
    public ResponseEntity<PageDTO<BrandDTO>> queryBrandByPage(Integer page, Integer rows, String key) {
        return ResponseEntity.ok(brandService.queryBrandByPage(page, rows, key));
    }


    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return  ResponseEntity<BrandDTO>
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> queryBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(brandService.queryBrandById(id));
    }

    /**
     * 新增品牌
     * @param brandDTO 品牌对象
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(BrandDTO brandDTO) {
        brandService.saveBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改品牌
     * @param brandDTO 品牌及分类信息
     * @return 无
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandDTO brandDTO) {
        brandService.updateBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除品牌
     * @param id 要删除的品牌id
     * @return 无
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable("id") Long id){
        brandService.deleteBrandById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
