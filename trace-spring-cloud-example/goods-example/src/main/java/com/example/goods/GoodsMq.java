package com.example.goods;

import com.example.goods.dao.GoodsDao;
import com.example.goods.request.ReductionStockMq;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsMq {

    @Autowired
    GoodsDao goodsDao;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "trace.test"),
            key = "goods.stock.reduction",
            value = @Queue("trace.test.goods")
    ))
    public void reductionStock(ReductionStockMq reductionStockMq){
        goodsDao.reductionStock(reductionStockMq.getGoodsId(),reductionStockMq.getCount());
    }

}
