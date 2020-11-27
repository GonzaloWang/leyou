package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecGroupServiceImpl extends ServiceImpl<SpecGroupMapper, SpecGroup> implements SpecGroupService {

    @Autowired
    private SpecParamService specParamService;

    /**
     * 根据商品分类id，查询规格组的集合
     *
     * @param id category_id
     * @return ResponseEntity<List < SpecGroupDTO>>
     */
    @Override
    public List<SpecGroupDTO> querySpecGroupByGoodsCategoryId(Long id) {
        return SpecGroupDTO.convertEntityList(query().eq("category_id", id).list());
    }

    /**
     * 新增规格组
     *
     * @param specGroupDTO categoryId  id name
     */
    @Override
    public void addSpecGroup(SpecGroupDTO specGroupDTO) {
        save(specGroupDTO.toEntity(SpecGroup.class));
    }

    /**
     * 修改规格组
     *
     * @param specGroupDTO categoryId  id name
     */
    @Override
    public void updateSpecGroup(SpecGroupDTO specGroupDTO) {
        updateById(specGroupDTO.toEntity(SpecGroup.class));
    }


    /**
     * 根据分类id查询规格组及组内参数
     * @param id  分类id
     * @return  ResponseEntity<List<SpecGroupDTO>>
     */
    @Override
    public List<SpecGroupDTO> querySpecGroupInfoByGoodsCategoryId(Long id) {
        // 查询规格组
        List<SpecGroupDTO> groupList = SpecGroupDTO.convertEntityList(query().eq("category_id", id).list());

        if (CollectionUtils.isEmpty(groupList)) {
            throw new LyException(400,"该分类下的规格组不存在!");
        }
        // 查询规格参数
        List<SpecParamDTO> paramList = specParamService.querySpecParamByTerm(id, null, null);
        // 对规格参数分组，groupId一致的在一组
        Map<Long, List<SpecParamDTO>> map = paramList.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));

        for (SpecGroupDTO groupDTO : groupList) {
            groupDTO.setParams(map.get(groupDTO.getId()));
        }
        return groupList;
    }

    @Override
    public void deleteSpecGroup(Long id) {

    }

    @Override
    public List<SpecGroupDTO> querySpecList(Long cid) {
        List<SpecGroupDTO> specGroupDTOS = querySpecGroupByGoodsCategoryId(cid);
//        specGroupDTOS.forEach(specGroupDTO -> {
//            specGroupDTO.setParams(SpecParamDTO.convertEntityList(this.specParamService.query().eq("group_id",cid).list()));
//        });
        List<SpecParamDTO> specParamDTOS = SpecParamDTO
                .convertEntityList(
                        this.specParamService
                                .query()
                                .eq("category_id", cid)
                                .list());

        // 对specParamDTOS进行分组,按照group_id分组, map的key就是group_id,value就是此id对应的规格参数集合
        Map<Long, List<SpecParamDTO>> paramMap = specParamDTOS.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));

        //循环给每个group赋值
        specGroupDTOS.forEach(specGroupDTO -> specGroupDTO.setParams(paramMap.get(specGroupDTO.getId())));
        for (SpecGroupDTO specGroupDTO : specGroupDTOS) {
            System.out.println(specGroupDTO.toString());
        }
        return specGroupDTOS;
    }


}
