package com.example.user.dao;

import com.example.user.model.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("select * from user where user_id = #{VALUE}")
    User get(Long userId);

}
