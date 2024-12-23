package com.css.coupon_sale.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class RoleBaseTEst {
    @GetMapping("/content")
    public String test(){
        return "Hello This is admin Content";
    }
}
