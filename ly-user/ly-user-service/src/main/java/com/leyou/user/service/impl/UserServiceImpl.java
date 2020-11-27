package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.config.PasswordConfig;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.KEY_PREFIX;
import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private StringRedisTemplate redisTemplate;

    private AmqpTemplate amqpTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    public UserServiceImpl(StringRedisTemplate redisTemplate, AmqpTemplate amqpTemplate) {
        this.redisTemplate = redisTemplate;
        this.amqpTemplate = amqpTemplate;
    }


    @Override
    public Boolean exists(String data, Integer type) {
        if(type != 1 && type != 2){
            throw new LyException(400, "请求参数有误");
        }
        // 校验手机或用户名是否存在
        return query()
                .eq(type == 1, "username", data)
                .eq(type == 2, "phone", data)
                .count() == 1;
    }

    @Override
    public void sendCode(String phone) {
        // 1.验证手机号格式
        if (!RegexUtils.isPhone(phone)) {
            throw new LyException(400, "请求参数有误");
        }

        // 2.使用Apache的工具类生成6位数字验证码
        String code = RandomStringUtils.randomNumeric(6);

        // 3.保存验证码到redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);

        // 4.发送RabbitMQ消息到ly-sms
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);
    }

    @Transactional
    public void register(User user, String code) {
        // 1.校验验证码
        // 1.1 取出redis中的验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        // 1.2 比较验证码
        if (!StringUtils.equals(code, cacheCode)) {
            throw new LyException(400, "验证码错误");
        }
        // 2.对密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3.写入数据库
        save(user);
    }


    public UserDTO queryUserByPhoneAndPassword(String username, String password) {
        // 1.根据用户名查询
        User user = getOne(new QueryWrapper<User>().eq("username", username));
        // 2.判断是否存在
        if (user == null) {
            // 用户名错误
            throw new LyException(400, "用户名或密码错误");
        }

        // 3.校验密码
        if(!passwordEncoder.matches(password, user.getPassword())){
            // 密码错误
            throw new LyException(400, "用户名或密码错误");
        }
        // 4.转换DTO
        return new UserDTO(user);
    }
}