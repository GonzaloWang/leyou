package com.leyou.search.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SearchRequest {
    private String key;
    private Integer page;
    private String sortBy;
    private Boolean desc;
    private Map<String,String> filters;
    private Integer rows = 20;
}
