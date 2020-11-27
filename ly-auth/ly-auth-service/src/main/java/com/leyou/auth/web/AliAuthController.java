package com.leyou.auth.web;

import com.leyou.auth.dto.AliOssSignatureDTO;
import com.leyou.auth.service.AliAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阿里的相关授权接口
 *
 *
 */
@RestController
@RequestMapping("ali")
public class AliAuthController {

    private AliAuthService aliAuthService;

    public AliAuthController(AliAuthService aliAuthService) {
        this.aliAuthService = aliAuthService;
    }

    /**
     * 申请oss签名
     *
     * @return 包含签名、图片验证策略等信息
     */
    @GetMapping("/oss/signature")
    public ResponseEntity<AliOssSignatureDTO> getAliSignature() {
        return ResponseEntity.ok(aliAuthService.getSignature());
    }

}