package com.leyou.item.web;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecGroupService specGroupService;

    @Autowired
    private SpecParamService specParamService;

    /**
     * 根据商品分类id，查询规格组的集合
     * @param id category_id
     * @return ResponseEntity<List<SpecGroupDTO>>
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroupByGoodsCategoryId(@RequestParam("id")Long id) {
        return ResponseEntity.ok(specGroupService.querySpecGroupByGoodsCategoryId(id));
    }


    /**
     * 根据条件查询规格参数集合
     * @return ResponseEntity<List<SpecParamDTO>>
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParamByTerm(
            @RequestParam(value = "categoryId",required = false)Long categoryId,
            @RequestParam(value = "groupId",required = false)Long groupId,
            @RequestParam(value = "searching",required = false)Boolean searching
    ) {
        System.out.println(groupId);
        return ResponseEntity.ok(specParamService.querySpecParamByTerm(categoryId,groupId,searching));
    }

    /**
     * 新增规格组
     * @param specGroupDTO  categoryId  id name
     * @return ResponseEntity<Void>
     */
    @PostMapping("/group")
    public ResponseEntity<Void> addSpecGroup(@RequestBody SpecGroupDTO specGroupDTO) {
        specGroupService.addSpecGroup(specGroupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格组
     * @param specGroupDTO categoryId  id name
     * @return ResponseEntity<Void>
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroupDTO specGroupDTO) {
        specGroupService.updateSpecGroup(specGroupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/group")
    public ResponseEntity<Void> deleteSpecGroup(@RequestParam("id")Long id) {
        specGroupService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据分类id查询规格组及组内参数
     * @param id  分类id
     * @return  ResponseEntity<List<SpecGroupDTO>>
     */
    @GetMapping("/list")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroupInfoByGoodsCategoryId(@RequestParam("id")Long id) {
        return ResponseEntity.ok(specGroupService.querySpecGroupInfoByGoodsCategoryId(id));
    }

    /**
     * 新增规格参数
     * @param specParamDTO  SpecParamDTO
     * @return esponseEntity<Void>
     */
    @PostMapping("/param")
    public ResponseEntity<Void> addSpecParam(@RequestBody SpecParamDTO specParamDTO) {
        specParamService.addSpecParam(specParamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格参数
     * @param specParamDTO  SpecParamDTO
     * @return esponseEntity<Void>
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParamDTO specParamDTO) {
        specParamService.updateSpecParam(specParamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据categoryid拆线呢组及组对应参数
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecList(@RequestParam("id")Long cid) {
        return ResponseEntity.ok(this.specGroupService.querySpecList(cid));
    }


}
