package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 根据品牌id，查询商品分类的集合
     *
     * @param id 品牌id
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @Select("SELECT * FROM tb_category tc INNER JOIN tb_category_brand tcb ON tc.id = tcb.category_id  WHERE tcb.brand_id = #{id}")
    List<Category> queryCategoriesByBrandId(@Param("id") Long id);
}
