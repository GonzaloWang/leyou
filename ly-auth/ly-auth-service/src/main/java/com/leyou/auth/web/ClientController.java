package com.leyou.auth.web;

import com.leyou.auth.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("key")
    public ResponseEntity<String> getSecretKey(@RequestParam("clientId") String clientId, @RequestParam("secret") String secret){
        return ResponseEntity.ok(clientService.getSecretKey(clientId, secret));
    }
}
