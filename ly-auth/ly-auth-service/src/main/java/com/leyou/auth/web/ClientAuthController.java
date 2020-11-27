package com.leyou.auth.web;

import com.leyou.auth.service.ClientAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientAuthController {

    @Autowired
    private ClientAuthService clientAuthService;

    /**
     * 其他服务请求这个方法获取 key (用户token加密用的)
     * @param clientId 服务名
     * @param secret secret
     * @return String key
     */
    @GetMapping
    public ResponseEntity<String> loadSecretKey(
            @RequestParam("clientId") String clientId,
            @RequestParam("secret")String secret) {


        return ResponseEntity.ok(clientAuthService.loadSecretKey(clientId, secret));
    }
}
