package com.leyou.page.dto;

import lombok.Data;


@Data
public class SpecParamNameDTO {
    private Long id;
    private String name;
    private Boolean numeric;
    private Boolean generic;
    private String unit;
}