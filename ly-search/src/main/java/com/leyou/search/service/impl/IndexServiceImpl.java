package com.leyou.search.service.impl;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.clients.ItemClients;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.respository.GoodsRepository;
import com.leyou.search.service.IndexService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GoodsRepository respository;

    @Autowired
    private ItemClients itemClients;


    /**
     * goods 对象的生成
     *
     * @param spuDTO
     * @return
     */
    @Override
    public Goods buildGoods(SpuDTO spuDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(spuDTO, goods);
        //categoryId
        goods.setCategoryId(spuDTO.getCid3());

        // suggestion
        List<String> suggest = new ArrayList<>();

        String goodsName = spuDTO.getName();
        String cName = spuDTO.getCategoryName().split("/")[2];
        String bName = spuDTO.getBrandName();
        suggest.add(cName);
        suggest.add(bName);
        // 把bname的()处理一下, 情况是: "华为（HUAWEI）手机",这样的
        bName = bName.replaceAll("\\（.*?\\）", "");
        suggest.add(bName + cName);
        suggest.add(cName + bName);
        suggest.add(goodsName);

        goods.setSuggestion(suggest);
        // 根据spuId查询对应的sku信息
        List<SkuDTO> skuDTOS = this.itemClients.querySkuBySpuId(spuDTO.getId());
        //image  这个方法是截取字符串逗号前的内容
        goods.setImage(StringUtils.substringBefore(skuDTOS.get(0).getImages(),"，"));



        // prices
        Set<Long> prices = new HashSet<>();
        Long sold = 0L;
        for (SkuDTO skuDTO : skuDTOS) {
            sold+=skuDTO.getSold();
            prices.add(skuDTO.getPrice());
        }
        goods.setPrices(prices);

        // sold
        goods.setSold(sold);

        // specs
        List<Map<String, Object>> specs = new ArrayList<>();
        List<SpecParamDTO> specParamDTOS = this.itemClients.querySpecsValues(spuDTO.getId(), true);
        specParamDTOS.forEach(specParamDTO -> {
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("name",specParamDTO.getName());
            resultMap.put("value",chooseSegment(specParamDTO));
            specs.add(resultMap);
        });

        goods.setSpecs(specs);

        //updateTime

        return goods;
    }

    private Object chooseSegment(SpecParamDTO p) {
        Object value = p.getValue();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        if (!p.getNumeric() || StringUtils.isBlank(p.getSegments()) || value instanceof Collection) {
            return value;
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * 把数据加载到elasticsearch
     */
    @Override
    public void loadData() {

        try {
            respository.deleteIndex();
        } catch (Exception e) {
        }
        respository.createIndex("{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"my_pinyin\": {\n" +
                "          \"tokenizer\": \"ik_smart\",\n" +
                "          \"filter\": [\n" +
                "            \"py\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"py\": {\n" +
                "\t\t  \"type\": \"pinyin\",\n" +
                "          \"keep_full_pinyin\": true,\n" +
                "          \"keep_joined_full_pinyin\": true,\n" +
                "          \"keep_original\": true,\n" +
                "          \"limit_first_letter_length\": 16,\n" +
                "          \"remove_duplicated_term\": true\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"suggestion\": {\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"image\":{\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"updateTime\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"specs\":{\n" +
                "        \"type\": \"nested\",\n" +
                "        \"properties\": {\n" +
                "          \"name\":{\"type\": \"keyword\" },\n" +
                "          \"value\":{\"type\": \"keyword\" }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");


        // 获取到spu,转换为goods,最终保存
        int page = 1;
        while (true) {
            PageDTO<SpuDTO> spuDTOPageDTO = this.itemClients.queryGoodsByPage(page, 50, null, null, null, null);


            if (spuDTOPageDTO.getTotalPage() == 0) {
                break;
            }
            List<Goods> goodsList = spuDTOPageDTO.getItems().stream().map(this::buildGoods).collect(Collectors.toList());


            respository.saveAll(goodsList);
            page++;

            if (page > spuDTOPageDTO.getTotalPage()) {
                break;
            }
        }
    }

    @Override
    public void saveGoodsById(Long spuId) {
        // 根据spuid查询对应的spu对象
        List<SpuDTO> items = this.itemClients.queryGoodsByPage(null, null, null, null, null, spuId).getItems();
        // 当值存在时把spudto转换为goods,然后保存
        if (!CollectionUtils.isEmpty(items)) {
            this.respository.save(buildGoods(items.get(0)));
        }
    }

    @Override
    public void deleteGoodsById(Long spuId) {
        this.respository.deleteById(spuId);
    }


}
