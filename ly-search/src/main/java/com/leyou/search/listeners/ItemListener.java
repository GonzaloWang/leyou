package com.leyou.search.listeners;

import com.leyou.search.service.IndexService;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.MQConstants.*;


@Component
public class ItemListener {

    @Autowired
    private IndexService indexService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.up.queue", durable = "true"),
            exchange = @Exchange(name = "jhj", type = ExchangeTypes.TOPIC),
            key = "up"
    ))
    public void listenItemUp(Long spuId){
        if (spuId != null) {
            // 商品上架，我们新增商品到索引库
            indexService.saveGoodsById(spuId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.down.queue", durable = "true"),
            exchange = @Exchange(name = "jhj", type = ExchangeTypes.TOPIC),
            key = "down"
    ))
    public void listenItemDown(Long spuId){
        if (spuId != null) {
            // 商品下架，我们删除商品
            indexService.deleteGoodsById(spuId);
        }
    }
}