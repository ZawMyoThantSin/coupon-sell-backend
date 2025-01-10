package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.response.PurchaseCouponResponse;
import com.css.coupon_sale.dto.response.QrDataResponse;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.service.QrCodeService;
import com.css.coupon_sale.service.SaleCouponService;
import com.css.coupon_sale.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-coupon")
public class SaleCouponController {
    @Autowired
    private SaleCouponService saleCouponService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private TransferService transferService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllCoupons(@PathVariable Long userId) {
        List<PurchaseCouponResponse> coupons = saleCouponService.getAllCouponsByUserId(userId);
        if (coupons.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no coupons available for the given user.");
        }
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/qr/{id}")
    public ResponseEntity<QrDataResponse> getBySaleCoupon(@PathVariable("id")Integer id){
        QrDataResponse response = qrCodeService.getBySaleCouponId(id);
        if (response !=null){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/transfer/{saleCouponId}/{acceptorId}")
    public ResponseEntity<Boolean> transferCoupon(@PathVariable("saleCouponId") int saleCouponId, @PathVariable("acceptorId") Long acceptorId) {
        boolean isTransferred = transferService.transferCoupon(saleCouponId, acceptorId);

        if (isTransferred) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(500).body(false);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getBySaleCouponId(@PathVariable("id") int saleCouponId) {
        PurchaseCouponResponse coupons = saleCouponService.getBySaleCouponId(saleCouponId);
        if (coupons == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no coupons available for the given user.");
        }
        return ResponseEntity.ok(coupons);
    }
}