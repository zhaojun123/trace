package com.example.goods.dao;

import com.example.goods.model.Goods;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface GoodsDao {

    @Select("select * from goods where goods_id = #{VALUE}")
    Goods get(Long goodsId);

    @Update("update goods set goods_stock = goods_stock - #{count} where goods_id = #{goodsId}")
    int reductionStock(Long goodsId,Long count);
}
