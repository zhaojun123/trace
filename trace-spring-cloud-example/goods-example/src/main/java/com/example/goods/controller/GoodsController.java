package com.example.goods.controller;

import com.example.goods.dao.GoodsDao;
import com.example.goods.model.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsDao goodsDao;

    @RequestMapping("/get")
    public Goods get(Long goodsId){
        Goods goods = goodsDao.get(goodsId);
        return goods;
    }
}
