package com.leyou.item.dto;

import com.leyou.common.dto.BaseDTO;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SkuDTO extends BaseDTO {
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private String specialSpec;// 商品特殊规格的键值对
    private String indexes;// 商品特殊规格的下标
    private Boolean saleable;// 是否有效，逻辑删除用
    private Integer stock; // 库存
    private Long sold; // 销量

    public SkuDTO(BaseEntity entity) {
        super(entity);
    }

    public static <T extends BaseEntity> List<SkuDTO> convertEntityList(Collection<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(SkuDTO::new).collect(Collectors.toList());
    }
}