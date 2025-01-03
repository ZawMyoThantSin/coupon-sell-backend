package com.css.coupon_sale.controller;

import com.css.coupon_sale.service.QrCodeService;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qrcodes")
public class QrCodeController {
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/generate/{saleCouponId}")
    public String saveCoupon(@PathVariable int saleCouponId) {
        boolean isSaved = qrCodeService.createAndSaveQrCode(saleCouponId);
        if (isSaved) {
            return "qr saved successfully.";
        } else {
            return "Failed to save qr.";
        }
    }
}
