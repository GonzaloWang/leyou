package com.leyou.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("address")
public class AddressController {

    @GetMapping("hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("上海浦东新区航头镇航头路18号传智播客");
    }
}