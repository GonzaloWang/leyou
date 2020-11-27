//package com.leyou.page.canal;
//
//import com.leyou.page.service.GoodsPageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import top.javatool.canal.client.annotation.CanalTable;
//import top.javatool.canal.client.context.CanalContext;
//import top.javatool.canal.client.handler.EntryHandler;
//
//import java.util.Map;
//
//
//@Slf4j
//@CanalTable(value = "all")
//@Component
//public class CanalHandler implements EntryHandler<Map<String,String>> {
//
//    @Autowired
//    private GoodsPageService goodsPageService;
//
//    @Override
//    public void insert(Map<String,String> model) {
//        // 获取表的名称
//        String table = CanalContext.getModel().getTable();
//        // 如果表是tb_sku
//        if("tb_sku".equals(table)){
//            log.info("sku新增了{}", model);
//            goodsPageService.loadSkuListData(Long.valueOf(model.get("spu_id")));
//        }
//    }
//
//    @Override
//    public void update(Map<String,String> before, Map<String,String> after) {
//        String table = CanalContext.getModel().getTable();
//        if("tb_sku".equals(table)){
//            log.info("sku修改了{}", after);
//            goodsPageService.loadSkuListData(Long.valueOf(after.get("spu_id")));
//        }
//    }
//
//    @Override
//    public void delete(Map<String,String> model) {
//        String table = CanalContext.getModel().getTable();
//        if("tb_sku".equals(table)){
//            log.info("sku删除了{}", model);
//            goodsPageService.deleteSku(Long.valueOf(model.get("spu_id")));
//        }
//    }
//}