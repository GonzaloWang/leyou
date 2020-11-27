package com.leyou.auth.service;

import com.leyou.auth.dto.AliOssSignatureDTO;


public interface AliAuthService {

    /**
     * 生成OSS的文件签名
     */
    AliOssSignatureDTO getSignature();
}