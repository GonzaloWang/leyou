package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;

import java.util.List;

public interface SpecParamService extends IService<SpecParam> {
    /**
     * 根据条件查询规格参数集合
     * @param categoryId  categoryId
     * @param groupId groupId
     * @param searching searching
     * @return   List<SpecParamDTO>
     */
    List<SpecParamDTO> querySpecParamByTerm(Long categoryId, Long groupId, Boolean searching);

    /**
     * 新增规格参数
     * @param specParamDTO  SpecParamDTO
     */
    void addSpecParam(SpecParamDTO specParamDTO);

    /**
     * 修改规格参数
     * @param specParamDTO  SpecParamDTO
     */
    void updateSpecParam(SpecParamDTO specParamDTO);

    List<SpecParamDTO> querySpecsValues(Long id, Boolean searching);
}
