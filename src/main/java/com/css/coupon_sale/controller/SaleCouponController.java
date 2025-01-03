package com.css.coupon_sale.controller;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accept")
public class SaleCouponController {
    @Autowired
    private SaleCouponService saleCouponService;

    @PostMapping("/generate/{orderId}")
    public String saveCoupon(@PathVariable int orderId) {
        boolean isSaved = saleCouponService.insertSaleCoupon(orderId);
        if (isSaved) {
            return "Sale coupon saved successfully.";
        } else {
            return "Failed to save sale coupon.";
        }
    }
}