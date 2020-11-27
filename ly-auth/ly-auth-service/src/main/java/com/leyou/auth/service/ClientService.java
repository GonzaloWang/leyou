package com.leyou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.auth.entity.ClientInfo;


public interface ClientService extends IService<ClientInfo> {
    String getSecretKey(String clientId, String secret);
}