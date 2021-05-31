package com.example.order.dao;

import com.example.order.model.Order;
import org.apache.ibatis.annotations.Insert;

public interface OrderDao {

    @Insert("insert into `order` (order_sn,user_id,goods_id,order_price)" +
            " values (#{orderSn},#{userId},#{goodsId},#{orderPrice})")
    int addOrder(Order order);

}
