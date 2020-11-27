package com.leyou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.entity.ClientInfo;
import com.leyou.auth.mapper.ClientInfoMapper;
import com.leyou.auth.service.ClientAuthService;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthServiceImpl extends ServiceImpl<ClientInfoMapper, ClientInfo> implements ClientAuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ly.jwt.key}")
    private  String key;

    @Override
    public String loadSecretKey(String clientId, String secret) {
        ClientInfo clientInfo = this.query().eq("client_id", clientId).one();
        if (null == clientInfo || !passwordEncoder.matches(secret,clientInfo.getSecret())) {
            throw new LyException(403, "请求服务非法,拒绝请求");
        }



        return key;
    }
}
