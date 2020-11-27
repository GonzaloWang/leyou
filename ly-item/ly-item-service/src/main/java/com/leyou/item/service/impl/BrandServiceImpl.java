package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryBrandService;
import com.leyou.item.service.CategoryService;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    private CategoryBrandService categoryBrandService;

    /**
     * 根据分类id查询品牌
     *
     * @param id 分类id
     * @return 品牌集合
     */
    @Override
    public List<BrandDTO> queryBrandByCategoryId(Long id) {
        List<CategoryBrand> categoryBrands = categoryBrandService.query().eq("category_id", id).list();
        List<Long> brands = categoryBrands.stream()
                .map(CategoryBrand::getBrandId)
                .collect(Collectors.toList());

        return BrandDTO.convertEntityList(listByIds(brands));
    }

    /**
     *根据关键字搜索品牌，并分页返回结果
     * @param page 当前页码
     * @param rows 每页大小
     * @param key 搜索条件
     * @return  ResponseEntity<PageDTO<BrandDTO>>
     */
    @Override
    public PageDTO<BrandDTO> queryBrandByPage(Integer page, Integer rows, String key) {
        // 1.分页信息的健壮性处理
        page = Math.min(page, 100);
        rows = Math.max(rows, 5);
        // 2.分页
        Page<Brand> info = new Page<>(page, rows);
        // 3.判断key是否存在
        boolean isKeyExists = StringUtils.isNoneBlank(key);
        // 4.如果key存在，添加like和eq的查询条件，否则不添加
        query().like(isKeyExists,"name", key)
                .or()
                .eq(isKeyExists,"letter",key)
                .page(info);
        // 5.封装结果
        List<Brand> brands = info.getRecords();
        return new PageDTO<>(info.getTotal(),info.getPages(),BrandDTO.convertEntityList(brands));
    }

    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return  ResponseEntity<BrandDTO>
     */
    @Override
    public BrandDTO queryBrandById(Long id) {
        return new BrandDTO(getById(id));
    }

    @Transactional
    @Override
    public void saveBrand(BrandDTO brandDTO) {
        Brand brand = brandDTO.toEntity(Brand.class);
        // 保存brand
        save(brand);
        // 封装中间表对象的集合
        List<CategoryBrand> list = brandDTO.getCategoryIds().stream()
                .map(id -> CategoryBrand.of(id, brand.getId()))
                .collect(Collectors.toList());
        // 批量写入中间表数据
        categoryBrandService.saveBatch(list);
    }

    @Transactional
    @Override
    public void updateBrand(BrandDTO brandDTO) {
        // 1.更新品牌
        boolean success = updateById(brandDTO.toEntity(Brand.class));
        if (!success) {
            // 更新失败，抛出异常
            throw new LyException(500, "更新品牌失败！");
        }
        // 2.根据品牌id删除中间表数据
        success = categoryBrandService.remove(
                new QueryWrapper<CategoryBrand>().eq("brand_id", brandDTO.getId()));
        if (!success) {
            // 更新失败，抛出异常
            throw new LyException(500,"更新品牌失败，删除中间表数据出错");
        }
        // 3.重新插入中间表数据
        List<CategoryBrand> list = brandDTO.getCategoryIds().stream()
                .map(id -> CategoryBrand.of(id, brandDTO.getId()))
                .collect(Collectors.toList());
        // 批量写入中间表数据
        categoryBrandService.saveBatch(list);
    }

    @Override
    public void deleteBrandById(Long id) {
        removeById(id);
        categoryBrandService.remove(
                new QueryWrapper<CategoryBrand>().eq("brand_id", id));
    }


}