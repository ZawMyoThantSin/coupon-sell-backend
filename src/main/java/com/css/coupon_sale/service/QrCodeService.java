package com.css.coupon_sale.service;

import org.springframework.stereotype.Service;

@Service
public interface QrCodeService {
    boolean createAndSaveQrCode(int saleCouponId);
}
