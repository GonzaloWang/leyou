package com.leyou.item.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_sku")
public class Sku extends BaseEntity {
    @TableId
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private Long sold;
    private Integer stock;
    private String specialSpec;
    private String indexes;
    private Boolean saleable;
}