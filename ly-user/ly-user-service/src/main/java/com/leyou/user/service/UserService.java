package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;

public interface UserService extends IService<User> {
    Boolean exists(String data, Integer type);

    void sendCode(String phone);

    void register(User user, String code);

    UserDTO queryUserByPhoneAndPassword(String phone, String password);
}