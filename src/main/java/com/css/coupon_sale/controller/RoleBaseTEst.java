package com.css.coupon_sale.controller;

import com.css.coupon_sale.websocket.WebSocketCustomHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class RoleBaseTEst {

    private final WebSocketCustomHandler webSocketCustomHandler;

    public RoleBaseTEst(WebSocketCustomHandler webSocketCustomHandler) {
        this.webSocketCustomHandler = webSocketCustomHandler;
    }


    @GetMapping("/hello/{message}")
    public String websocketTest(@PathVariable("message") String message){

        String response = "THis is testing from another port.";

        webSocketCustomHandler.sendToUser(1L, response);

                return "Successful";
    }

    @GetMapping("/content")
    public String test(){
        return "Hello This is admin Content";
    }


}
