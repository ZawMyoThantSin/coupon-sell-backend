package com.css.coupon_sale.controller;

import com.css.coupon_sale.entity.CouponValidationEntity;
import com.css.coupon_sale.entity.QrCodeEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.repository.CouponValidationRepository;
import com.css.coupon_sale.repository.QrCodeRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
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

    @Autowired
    private SaleCouponRepository saleCouponRepository;

    @Autowired
    private CouponValidationRepository couponValidationRepository;

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

    @GetMapping("/validate/{id}")
    public ResponseEntity<Boolean> validateUserCoupon(@PathVariable("id") Integer id){
        SaleCouponEntity saleCoupon = saleCouponRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Coupon Not Found!"));
        if (saleCoupon != null){
            try{
                CouponValidationEntity couponValidationEntity = new CouponValidationEntity();
                saleCoupon.setStatus(1);
                saleCouponRepository.save(saleCoupon);
                couponValidationEntity.setSaleCoupon(saleCoupon);
                couponValidationEntity.setCustomer(saleCoupon.getUser());
                couponValidationEntity.setShop(saleCoupon.getBusiness());
                couponValidationEntity.setUsedAt(LocalDateTime.now());
                couponValidationRepository.save(couponValidationEntity);

                return ResponseEntity.ok(true);
            }catch (Exception e){
                System.out.println("ERROR"+ e.getMessage());
                return ResponseEntity.ok(false);
            }
        }
        return ResponseEntity.notFound().build();
    }

    public record ResponseData(LocalDateTime expireDate, int status, int saleCouponId, int businessId){}
}
