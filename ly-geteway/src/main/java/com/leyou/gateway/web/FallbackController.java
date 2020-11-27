package com.leyou.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {
//    @GetMapping
//    public Map<String, String> fallback() {
//        HashMap<String, String> msg = new HashMap<>();
//        msg.put("status", "503");
//        msg.put("msg","网关超时");
//        return msg;
//    }

    @GetMapping("/hystrix/fallback")
    public ResponseEntity<String> fallback() {
        // 状态码 内容
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
    }
}
