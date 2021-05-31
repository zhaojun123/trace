package com.example.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user")
@RequestMapping("/user")
public interface UserClient {

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    User get(@RequestParam("userId") Long userId);

}
