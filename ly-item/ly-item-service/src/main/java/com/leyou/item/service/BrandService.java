package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;

import java.util.List;


public interface BrandService extends IService<Brand> {
    /**
     * 根据分类id查询品牌
     *
     * @param id 分类id
     * @return 品牌集合
     */
    List<BrandDTO> queryBrandByCategoryId(Long id);

    /**
     *根据关键字搜索品牌，并分页返回结果
     * @param page 当前页码
     * @param rows 每页大小
     * @param key 搜索条件
     * @return  ResponseEntity<PageDTO<BrandDTO>>
     */
    PageDTO<BrandDTO> queryBrandByPage(Integer page, Integer rows, String key);

    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return  ResponseEntity<BrandDTO>
     */
    BrandDTO queryBrandById(Long id);

    /**
     * 新增品牌和中间表数据
     * @param brandDTO 品牌的DTO
     */
    void saveBrand(BrandDTO brandDTO);

    /**
     * 更新品牌
     * @param brandDTO 品牌的DTO
     */
    void updateBrand(BrandDTO brandDTO);

    /**
     * 删除品牌
     * @param id 要删除的品牌id
     * @return 无
     */
    void deleteBrandById(Long id);
}