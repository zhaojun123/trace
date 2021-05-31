package com.example.discount.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discount")
public class DiscountController {

    /**
     * 双十一打折服务，所有价格都打五折
     * @param price
     * @return
     */
    @RequestMapping("/get")
    public Double get(Double price){
        if(price<=0){
            throw new IllegalArgumentException("价格必须大于0");
        }
        return price*0.5;
    }

}
