package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;

import java.util.List;

public interface SpecGroupService extends IService<SpecGroup> {
    /**
     * 根据商品分类id，查询规格组的集合
     * @param id category_id
     * @return ResponseEntity<List<SpecGroupDTO>>
     */
    List<SpecGroupDTO> querySpecGroupByGoodsCategoryId(Long id);

    /**
     * 新增规格组
     * @param specGroupDTO  categoryId  id name
     */
    void addSpecGroup(SpecGroupDTO specGroupDTO);

    /**
     * 修改规格组
     * @param specGroupDTO categoryId  id name
     */
    void updateSpecGroup(SpecGroupDTO specGroupDTO);


    /**
     * 根据分类id查询规格组及组内参数
     * @param id  分类id
     * @return  ResponseEntity<List<SpecGroupDTO>>
     */
    List<SpecGroupDTO> querySpecGroupInfoByGoodsCategoryId(Long id);


    void deleteSpecGroup(Long id);

    List<SpecGroupDTO> querySpecList(Long cid);

}
