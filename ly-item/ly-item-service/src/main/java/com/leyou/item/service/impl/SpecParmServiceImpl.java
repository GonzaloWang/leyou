package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.service.SpecParamService;
import com.leyou.item.service.SpuDetailService;
import com.leyou.item.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpecParmServiceImpl extends ServiceImpl<SpecParamMapper, SpecParam> implements SpecParamService {

    @Autowired
    private SpuDetailService detailService;

    @Autowired
    private SpuService spuService;

    /**
     * 根据分类id查询规格组及组内参数
     * @param categoryId  categoryId
     * @param groupId groupId
     * @param searching searching
     * @return   List<SpecParamDTO>
     */
    @Override
    public List<SpecParamDTO> querySpecParamByTerm(Long categoryId, Long groupId, Boolean searching) {
        if (categoryId == null && groupId == null) {
            throw new LyException(400, "查询条件不能为空!");
        }
        List<SpecParam> list = query().eq(categoryId != null, "category_id", categoryId)
                .eq(groupId != null, "group_id", groupId)
                .eq(searching != null, "searching", searching)
                .list();
        return SpecParamDTO.convertEntityList(list);
    }

    /**
     * 新增规格参数
     * @param specParamDTO  SpecParamDTO
     */
    @Override
    public void addSpecParam(SpecParamDTO specParamDTO) {
        save(specParamDTO.toEntity(SpecParam.class));
    }

    @Override
    public void updateSpecParam(SpecParamDTO specParamDTO) {
        updateById(specParamDTO.toEntity(SpecParam.class));
    }

    @Override
    public List<SpecParamDTO> querySpecsValues(Long id, Boolean searching) {
        SpuDetail spuDetail = detailService.getById(id);
        Map<Long, Object> specValues = JsonUtils.toMap(spuDetail.getSpecification(), Long.class, Object.class);

        Spu spu = spuService.getById(id);

        List<SpecParamDTO> params = querySpecParamByTerm(spu.getCid3(), null, searching);
        for (SpecParamDTO param : params) {
            param.setValue(specValues.get(param.getId()));
        }
        return params;
    }
}
