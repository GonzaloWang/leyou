package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.service.SpuDetailService;
import org.springframework.stereotype.Service;


@Service
public class SpuDetailServiceImpl extends ServiceImpl<SpuDetailMapper, SpuDetail> implements SpuDetailService {
    /**
     * 根据id查询SpuDetail
     */
    @Override
    public SpuDetailDTO queryDetailById(Long id) {
        SpuDetail spuDetail = getById(id);
        return new SpuDetailDTO(spuDetail);
    }
}
