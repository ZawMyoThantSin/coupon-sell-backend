package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.response.QrDataResponse;
import com.css.coupon_sale.entity.QrCodeEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.repository.QrCodeRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
import com.css.coupon_sale.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @Autowired
    private SaleCouponRepository saleCouponRepository;

    @Override
    public boolean createAndSaveQrCode(int saleCouponId) {
        SaleCouponEntity saleCoupon = saleCouponRepository.findById(saleCouponId)
                .orElseThrow(()->new RuntimeException("Row not found"));

        if(saleCoupon == null){
            return  false;
        }

        try {
            QrCodeEntity qrCode = new QrCodeEntity();
            qrCode.setSaleCoupon(saleCoupon);
            // Generate UUID + SaleCoupon data
            String qrCodeValue = generateQrCodeValue(saleCoupon);
            qrCode.setQrCode(qrCodeValue);
            qrCode.setBusiness(saleCoupon.getBusiness());
            qrCode.setStatus(0); // Example status
            qrCode.setExpiredDate(saleCoupon.getExpiredDate());
            qrCode.setGeneratedDate(LocalDateTime.now());
            qrCodeRepository.save(qrCode);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error creating and saving QR Code: " + e.getMessage(), e);
        }
    }

    @Override
    public QrDataResponse getBySaleCouponId(int saleCouponId) {
        QrCodeEntity qrCode = qrCodeRepository.findBySaleCoupon_Id(saleCouponId);
        if (qrCode!=null){
            QrDataResponse response = new QrDataResponse();
            response.setId(qrCode.getId());
            response.setQrcode(qrCode.getQrCode());
            response.setGeneratedDate(qrCode.getGeneratedDate());
            response.setStatus(qrCode.getStatus());
            response.setExpiredDate(qrCode.getExpiredDate());
            response.setBusinessName(qrCode.getBusiness().getName());
            response.setBusinessImage(qrCode.getBusiness().getPhoto());
            return  response;
        }
        return null;
    }

    private String generateQrCodeValue(SaleCouponEntity saleCoupon) {
        String uuid = UUID.randomUUID().toString();
        String saleCouponData = "SC-" + saleCoupon.getId() + "-" + saleCoupon.getBusiness().getId();
        return uuid + "-" + saleCouponData;
    }
}
