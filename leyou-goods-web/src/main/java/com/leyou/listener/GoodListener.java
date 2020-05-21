package com.leyou.listener;

import com.leyou.service.GoodHtmlService;
import com.leyou.service.impl.GoodHtmlServiceImpl;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodListener {
    @Autowired
    private GoodHtmlServiceImpl goodHtmlService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "LEYOU.GOOD.CREATE.QUEUE", durable = "true"),
                     exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",
                                ignoreDeclarationExceptions = "true",
                                type = ExchangeTypes.TOPIC),
                               key = {"item.insert","item.update"}))
    public void save(Long id) throws Exception{
        if (id == null){
            return;
        }
        goodHtmlService.asyncExcute(id);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "LEYOU.GOOD.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void delete(Long id) throws Exception{
        if (id == null){
            return;
        }
        goodHtmlService.deleteById(id);
    }
}
