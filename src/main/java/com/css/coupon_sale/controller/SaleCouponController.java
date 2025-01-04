package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.response.PurchaseCoupon;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salecoupon")
public class SaleCouponController {
    @Autowired
    private SaleCouponService saleCouponService;

    @PostMapping("{orderId}")
    public String saveCoupon(@PathVariable int orderId) {
        boolean isSaved = saleCouponService.insertSaleCoupon(orderId);
        if (isSaved) {
            return "Sale coupon saved successfully.";
        } else {
            return "Failed to save sale coupon.";
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllCoupons(@PathVariable Long userId) {
        List<PurchaseCoupon> coupons = saleCouponService.getAllCouponsBYUserId(userId);
        if (coupons.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no coupons available for the given user.");
        }
        return ResponseEntity.ok(coupons);
    }

}