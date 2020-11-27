package com.leyou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.entity.ClientInfo;
import com.leyou.auth.mapper.ClientMapper;
import com.leyou.auth.service.ClientService;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, ClientInfo> implements ClientService {

    private final PasswordEncoder passwordEncoder;

    @Value("${ly.jwt.key}")
    private String key;

    public ClientServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getSecretKey(String clientId, String secret) {
        // 1.查询client信息
        ClientInfo client = query().eq("client_id", clientId).one();
        if (client == null) {
            throw new LyException(401, "客户端的信息有误，" + clientId + "不存在！");
        }
        // 2.校验client的secret
        if (!passwordEncoder.matches(secret, client.getSecret())) {
            throw new LyException(401, "客户端的信息有误，secret不正确！");
        }
        // 3.返回秘钥
        return key;
    }
}