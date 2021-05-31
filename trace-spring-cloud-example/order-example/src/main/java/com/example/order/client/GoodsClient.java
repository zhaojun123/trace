package com.example.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("goods")
@RequestMapping("/goods")
public interface GoodsClient {

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    Goods get(@RequestParam("goodsId") Long goodsId);
}
