package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryBrandService;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

//    @Autowired
//    private CategoryBrandService categoryBrandService;

    /**
     * 根据pid查询对应的分类集合(多行数据)
     * @param pid 数据库中tb_category的parent_id字段
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @Override
    public List<CategoryDTO> queryCategoryByPid(Long pid) {
        // select * from tb_category where praent_id = #{pid};
        query().list();
        List<Category> categories = query().eq("parent_id", pid).list();
        return CategoryDTO.convertEntityList(categories);
    }

    /**
     *根据id查询对应的分类(一行数据)
     * @param id  数据库中tb_category的id字段
     * @return ResponseEntity<CategoryDTO>
     */
    @Override
    public CategoryDTO queryCategoryById(Long id) {
        return new CategoryDTO(this.getById(id));
    }

    /**
     * 根据分类id的集合，查询商品分类的集合
     * @param ids  分类id的集合
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @Override
    public List<CategoryDTO> queryCategoryByIds(List<Long> ids) {
        // SELECT * FROM `tb_category` WHERE id IN (1,2,3,4);
        return CategoryDTO.convertEntityList(listByIds(ids));
    }

    /**
     * 根据品牌id，查询商品分类的集合
     * @param id 品牌id
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @Override
    public List<CategoryDTO> queryCategoriesByBrandId(Long id) {
//        // 1.根据品牌id，查询中间表，得到中间表对象集合
//        List<CategoryBrand> categoryBrandList = categoryBrandService.query().eq("brand_id", id).list();
//        if (CollectionUtils.isEmpty(categoryBrandList)) {
//            return Collections.emptyList();
//        }
//        // 2.获取分类id集合
//        List<Long> CategoryIds = categoryBrandList.stream()
//                .map(CategoryBrand::getCategoryId)
//                .collect(Collectors.toList());
//        // 3.根据分类id集合，查询分类对象集合
//        List<Category> categories = listByIds(CategoryIds);
//        return CategoryDTO.convertEntityList(categories);

        // 直接sql查询:
        return CategoryDTO.convertEntityList(this.baseMapper.queryCategoriesByBrandId(id));
    }
}
