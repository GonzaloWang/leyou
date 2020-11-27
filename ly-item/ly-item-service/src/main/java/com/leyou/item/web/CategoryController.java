package com.leyou.item.web;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据pid查询对应的分类集合(多行数据)
     * @param pid 数据库中tb_category的parent_id字段
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByPid(@RequestParam("pid") Long pid) {
        return ResponseEntity.ok(this.categoryService.queryCategoryByPid(pid));
    }

    /**
     *根据id查询对应的分类(一行数据)
     * @param id  数据库中tb_category的id字段
     * @return ResponseEntity<CategoryDTO>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> queryCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.categoryService.queryCategoryById(id));
    }

    /**
     * 根据分类id的集合，查询商品分类的集合
     * @param ids  分类id的集合
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByIds(@RequestParam("ids")List<Long> ids) {
        return ResponseEntity.ok(this.categoryService.queryCategoryByIds(ids));
    }

    /**
     * 根据品牌id，查询商品分类的集合
     * @param id 品牌id
     * @return ResponseEntity<List<CategoryDTO>>
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> queryCategoriesByBrandId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(this.categoryService.queryCategoriesByBrandId(id));
    }




}
