package com.leyou.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leyou.common.dto.BaseDTO;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpuDTO extends BaseDTO {
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String name;// 名称
    private String title;// 标题
    private Boolean saleable;// 是否上架
    private String categoryName; // 商品分类名称拼接
    private String brandName;// 品牌名称
    /**
     * 商品详情
     */
    private SpuDetailDTO spuDetail;
    /**
     * spu下的sku的集合
     */
    private List<SkuDTO> skus;
    /**
     * 方便同时获取3级分类
     * @return 3级分类的id集合
     */
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return Arrays.asList(cid1, cid2, cid3);
    }


    public SpuDTO(BaseEntity entity) {
        super(entity);
    }

    public static <T extends BaseEntity> List<SpuDTO> convertEntityList(Collection<T> list){
        if(list == null){
			return Collections.emptyList();
		}
        return list.stream().map(SpuDTO::new).collect(Collectors.toList());
    }
}