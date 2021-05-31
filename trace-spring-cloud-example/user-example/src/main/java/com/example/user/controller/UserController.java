package com.example.user.controller;

import com.example.user.dao.UserDao;
import com.example.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserDao userDao;

    @RequestMapping("/get")
    public User get(Long userId){
        return userDao.get(userId);
    }

}
