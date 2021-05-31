package com.example.order.controller;

import com.example.order.client.Goods;
import com.example.order.client.GoodsClient;
import com.example.order.client.User;
import com.example.order.client.UserClient;
import com.example.order.dao.OrderDao;
import com.example.order.model.Order;
import com.example.order.request.AddOrder;
import com.example.order.request.ReductionStockMq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    UserClient userClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderDao orderDao;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 下单
     * @param addOrder
     * @return
     */
    @RequestMapping("/addOrder")
    public String addOrder(AddOrder addOrder){
        //查询商品信息，计算订单价格
        Goods goods = goodsClient.get(addOrder.getGoodsId());
        Double orderPrice = goods.getGoodsPrice()*addOrder.getCount();
        //查询用户信息
        User user = userClient.get(addOrder.getUserId());
        //如果该用户是VIP用户，那么要打上一个折扣
        if("YES".equals(user.getVip())){
            //这里因为某个bug，导致订单价格变为0
            orderPrice = 0d;
            logger.info("vip用户：【{}】 订单打折价格 【{}】",user.getUserName(),orderPrice);
        }
        //双十一打折活动 因为discount是在云计算平台，这里使用restTemplate调用
        orderPrice = restTemplate.getForObject("http://localhost:8085/discount/get?price="+orderPrice,Double.class);

        Order order = new Order();
        order.setGoodsId(addOrder.getGoodsId());
        order.setOrderPrice(orderPrice);
        order.setOrderSn(UUID.randomUUID().toString());
        order.setUserId(addOrder.getUserId());
        orderDao.addOrder(order);

        //将减库存信息放入到消息队列中
        ReductionStockMq reductionStockMq = new ReductionStockMq();
        reductionStockMq.setGoodsId(goods.getGoodsId());
        reductionStockMq.setCount(addOrder.getCount());
        rabbitTemplate.convertAndSend("trace.test","goods.stock.reduction",reductionStockMq);
        return "SUCCESS";
    }

}
