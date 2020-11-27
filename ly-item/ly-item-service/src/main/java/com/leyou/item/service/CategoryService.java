package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 根据pid查询对应的分类集合(多行数据)
     * @param pid 数据库中tb_category的parent_id字段
     * @return ResponseEntity<List<CategoryDTO>>
     */
    List<CategoryDTO> queryCategoryByPid(Long pid);

    /**
     *根据id查询对应的分类(一行数据)
     * @param id  数据库中tb_category的id字段
     * @return ResponseEntity<CategoryDTO>
     */
    CategoryDTO queryCategoryById(Long id);

    /**
     * 根据分类id的集合，查询商品分类的集合
     * @param ids  分类id的集合
     * @return ResponseEntity<List<CategoryDTO>>
     */
    List<CategoryDTO> queryCategoryByIds(List<Long> ids);

    /**
     * 根据品牌id，查询商品分类的集合
     * @param id 品牌id
     * @return ResponseEntity<List<CategoryDTO>>
     */
    List<CategoryDTO> queryCategoriesByBrandId(Long id);
}
