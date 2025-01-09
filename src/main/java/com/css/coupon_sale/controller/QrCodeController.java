package com.css.coupon_sale.controller;

import com.css.coupon_sale.entity.QrCodeEntity;
import com.css.coupon_sale.repository.QrCodeRepository;
import com.css.coupon_sale.service.QrCodeService;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {
    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @GetMapping("/code/{qrCode}")
    public ResponseEntity<ResponseData> findByQrCode(@PathVariable("qrCode")String qrCode){
        QrCodeEntity qrCode1 =  qrCodeRepository.findByQrCode(qrCode);
        ResponseData responseData =new ResponseData(
                qrCode1.getExpiredDate(),
                qrCode1.getStatus(),
                qrCode1.getSaleCoupon().getId(),
                qrCode1.getBusiness().getId()
                );
        return ResponseEntity.ok(responseData);
    }

    public record ResponseData(LocalDateTime expireDate, int status, int saleCouponId, int businessId){}
}
