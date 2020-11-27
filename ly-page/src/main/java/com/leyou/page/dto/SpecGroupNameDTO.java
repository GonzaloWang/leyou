package com.leyou.page.dto;

import lombok.Data;

import java.util.List;


@Data
public class SpecGroupNameDTO {
    private String name;
    private List<SpecParamNameDTO> params;
}