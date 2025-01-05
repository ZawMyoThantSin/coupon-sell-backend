package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.response.QrDataResponse;
import org.springframework.stereotype.Service;

@Service
public interface QrCodeService {
    boolean createAndSaveQrCode(int saleCouponId);

    QrDataResponse getBySaleCouponId(int saleCouponId);
}
