package com.leyou.search.service;

import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;

public interface IndexService {

    Goods buildGoods(SpuDTO spuDTO);

    void loadData();

    void saveGoodsById(Long spuId);

    void deleteGoodsById(Long spuId);
}
