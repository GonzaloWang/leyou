package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Spu;
import org.springframework.transaction.annotation.Transactional;


public interface SpuService extends IService<Spu> {
    /**
     * 分页查询spu
     *
     * @param page     当前页
     * @param rows     每页大小
     * @param saleable 上架商品或下降商品
     * @param brandId 品牌id
     * @param categoryId 分类id
     * @param id  spu的id
     * @return 当前页商品数据
     */
    PageDTO<SpuDTO> queryGoodsByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id);

    /**
     * 新增商品
     * @param spuDTO 页面提交商品信息
     * @return 无
     */
    void saveGoods(SpuDTO spuDTO);


    /**
     * 上下架
     * @param id id
     * @param saleable 表中上下架的字段
     */
    void updateSaleable(Long id, Boolean saleable);

    /**
     * 根据ID查询商品
     * @param id id
     * @return 商品
     */
    SpuDTO queryGoodsById(Long id);

    /**
     * 修改商品
     * @param spuDTO spuDto对象
     */
    void updateGoods(SpuDTO spuDTO);
}
