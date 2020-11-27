package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.service.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements SpuService {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuDetailService detailService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


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
    @Override
    public PageDTO<SpuDTO> queryGoodsByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id) {
        // 1.健壮性
        int current = Math.max(page, 1);
        int size = Math.max(rows, 5);

        // 2.准备查询条件， SELECT * FROM tb_spu WHERE saleable = ? AND category_id = ? AND brand_id = ? AND id = ?
        Page<Spu> result = query()
                .eq(saleable != null, "saleable", saleable)
                .eq(categoryId != null, "cid3", categoryId)
                .eq(brandId != null, "brand_id", brandId)
                .eq(id != null, "id", id)
                // 3.准备分页条件 LIMIT ?, ?
                .page(new Page<>(current, size));

        // 4.解析查询结果
        long total = result.getTotal();
        long pages = result.getPages();
        List<Spu> list = result.getRecords();

        // 5.转换DTO
        List<SpuDTO> dtoList = SpuDTO.convertEntityList(list);
        for (SpuDTO spuDTO : dtoList) {
            // 查询spu的分类和品牌的名称
            handleCategoryAndBrandName(spuDTO);
        }

        // 6.封装分页结果并返回
        return new PageDTO<>(total, pages, dtoList);
    }

    /**
     * 新增商品
     *
     * @param spuDTO 页面提交商品信息
     * @return 无
     */
    @Transactional
    @Override
    public void saveGoods(SpuDTO spuDTO) {
        // 添加 spu
        Spu spu = spuDTO.toEntity(Spu.class);
        spu.setSaleable(false);
        boolean success = save(spu);
        if (!success) {
            throw new LyException(500, "新增商品失败");
        }
        // 添加 spudetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        success = detailService.save(spuDetail.toEntity(SpuDetail.class));
        if (!success) {
            throw new LyException(500, "新增商品详情失败");
        }

        // 添加sku
        List<SkuDTO> skus = spuDTO.getSkus();
        List<Sku> skuList = skus.stream().map(skuDTO -> {
            Sku sku = skuDTO.toEntity(Sku.class);
            sku.setSaleable(false);
            sku.setSpuId(spu.getId());
            return sku;
        }).collect(Collectors.toList());

        skuService.saveBatch(skuList);
    }

    /**
     * 修改商品
     *
     * @param id       id spu表id字段
     * @param saleable saleable字段
     */
    @Override
    @Transactional
    public void updateSaleable(Long id, Boolean saleable) {
        // 1.更新SPU
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
        updateById(spu);
        // 更新sku
        Sku sku = new Sku();
        sku.setSaleable(saleable);

        this.skuService.update(sku, new QueryWrapper<Sku>()
                .eq("spu_id", id)
                .eq("saleable", !saleable));
        // saleable为true上架,为false时下架
        String routingKey = saleable ? "up" : "down";
        // 放入消息队列
        this.amqpTemplate.convertAndSend("jhj", routingKey, spu.getId());
    }

    /**
     * 根据id查询商品
     *
     * @param id spu表id字段
     * @return
     */
    @Override
    public SpuDTO queryGoodsById(Long id) {
        SpuDTO spuDTO = queryGoodsByPage(1, 1, null, null, null, id).getItems().get(0);
        SpuDetailDTO spuDetailDTO = new SpuDetailDTO(detailService.getById(id));
        List<SkuDTO> skuDTOS = SkuDTO.convertEntityList(skuService.query().eq("spu_id", id).list());
        spuDTO.setSkus(skuDTOS);
        spuDTO.setSpuDetail(spuDetailDTO);
        return spuDTO;
    }

    /**
     * 修改商品
     *
     * @param spuDTO spuDto对象
     */
    @Override
    public void updateGoods(SpuDTO spuDTO) {
        //若id存在 修改spu
        if (null != spuDTO.getId()) {
            boolean success = updateById(spuDTO.toEntity(Spu.class));
            if (!success) {
                throw new LyException(500, "修改失败");
            }
        }
        // 修改spudetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        if (spuDetail != null && spuDetail.getSpuId() != null) {
            SpuDetail spuDetail1 = spuDetail.toEntity(SpuDetail.class);
            boolean success = detailService.updateById(spuDetail1);
            if (!success) {
                // 更新失败，抛出异常
                throw new LyException(500, "更新商品失败！");
            }
        }

        // 修改sku
        List<SkuDTO> skus = spuDTO.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            return;
        }
        Map<Boolean, List<Sku>> map = skus.stream().
                map(skuDTO -> skuDTO.toEntity(Sku.class))
                .collect(Collectors.groupingBy(sku -> sku.getSaleable() == null));
        List<Sku> insertOrUpdateList = map.get(true);
        if (!CollectionUtils.isEmpty(insertOrUpdateList)) {
            skuService.saveOrUpdateBatch(insertOrUpdateList);
        }
        List<Sku> deleteList = map.get(false);
        if (!CollectionUtils.isEmpty(deleteList)) {
            List<Long> ids = deleteList.stream().map(Sku::getId).collect(Collectors.toList());
            skuService.removeByIds(ids);
        }

    }


    private void handleCategoryAndBrandName(SpuDTO spuDTO) {
        // 根据品牌id查询品牌名称
        Brand brand = brandService.getById(spuDTO.getBrandId());
        if (brand != null) {
            spuDTO.setBrandName(brand.getName());
        }
        // 根据三级分类id查询分类集合
        List<Category> categories = categoryService.listByIds(spuDTO.getCategoryIds());
        if (!CollectionUtils.isEmpty(categories)) {
            // 取出分类的名称，拼接起来
            String names = categories.stream().map(Category::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(names);
        }
//        Long brandId = spuDTO.getBrandId();
//        List<Long> categoryIds = spuDTO.getCategoryIds();
//
//
//        if (redisTemplate.hasKey(brandId)) {
//            // 查找redis中有没有该数据 redis中的数据结构:key:value
//            spuDTO.setBrandName((String) redisTemplate.opsForValue().get(brandId));
//        } else {
//            // 根据品牌id查询品牌名称
//            Brand brand = brandService.getById(brandId);
//            if(brand != null) {
//                spuDTO.setBrandName(brand.getName());
//                // 放入redis中
//                redisTemplate.opsForValue().set(brandId,brand.getName());
//            }
//        }
//
//        if (redisTemplate.hasKey(categoryIds)) {
//            // 查找redis中有没有该数据 redis中的数据结构 [12,12,12] : value
//            spuDTO.setCategoryName((String) redisTemplate.opsForValue().get(categoryIds));
//        } else {
//            // 根据三级分类id查询分类集合
//            List<Category> categories = categoryService.listByIds(categoryIds);
//            if(!CollectionUtils.isEmpty(categories)) {
//                // 取出分类的名称，拼接起来
//                String names = categories.stream().map(Category::getName).collect(Collectors.joining("/"));
//                spuDTO.setCategoryName(names);
//                // 放入redis中
//                redisTemplate.opsForValue().set(categoryIds, names);
//        }
//
//        }
    }


}
