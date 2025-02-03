package com.css.coupon_sale.controller;

import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.response.BusinessCouponSalesResponse;
import com.css.coupon_sale.dto.response.BusinessEarningsDTO;
import com.css.coupon_sale.dto.response.PurchaseCouponResponse;
import com.css.coupon_sale.dto.response.QrDataResponse;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.service.CouponService;
import com.css.coupon_sale.service.QrCodeService;
import com.css.coupon_sale.service.SaleCouponService;
import com.css.coupon_sale.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale-coupon")
public class SaleCouponController {
    @Autowired
    private SaleCouponService saleCouponService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private CustomWebSocketHandler webSocketHandler;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBySaleCouponId(@PathVariable("id") int saleCouponId) {
        PurchaseCouponResponse coupons = saleCouponService.getBySaleCouponId(saleCouponId);
        if (coupons == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no coupons available for the given user.");
        }
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/transfer/{saleCouponId}/{acceptorId}")
    public ResponseEntity<Boolean> transferCoupon(@PathVariable("saleCouponId") int saleCouponId, @PathVariable("acceptorId") Long acceptorId) {
        try {
            boolean isTransferred = transferService.transferCoupon(saleCouponId, acceptorId);

            if (isTransferred) {
                // Send WebSocket message
                String message = "COUPON_TRANSFER_TRANSFERRED";
                webSocketHandler.sendToUser(acceptorId, message);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(500).body(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @GetMapping("/coupon-sales/{id}")
    public ResponseEntity<?> getSoldCouponCountByBusiness(@PathVariable("id") Integer businessId) {
        try {
            List<BusinessCouponSalesResponse> salesData = couponService.getSoldCouponCountByBusiness(businessId);
            return ResponseEntity.status(200).body(salesData);
        }catch (Exception e){
            System.out.println("ERR Conn:" + e.getMessage());
        }
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/business-earnings")
    public List<BusinessEarningsDTO> getBusinessEarnings() {
        return saleCouponService.getBusinessEarnings();
    }

    @GetMapping("/business-earnings/{businessId}")
    public BusinessEarningsDTO getEarningsByBusinessId(@PathVariable int businessId) {
        return saleCouponService.getEarningsByBusinessId(businessId);
    }

    @GetMapping("/business-earnings/monthly/{businessId}")
    public Double getCurrentMonthEarnings(@PathVariable int businessId) {
        return saleCouponService.getCurrentMonthEarnings(businessId);
    }
    @GetMapping("/business-earnings/yearly/{businessId}")
    public Double getCurrentYearEarnings(@PathVariable int businessId) {
        return saleCouponService.getCurrentYearEarnings(businessId);
    }

    @GetMapping("/months/{businessId}")
    public ResponseEntity<Map<String, Double>> getAvailableMonths(@PathVariable int businessId) {
        Map<String, Double> monthEarnings = saleCouponService.getExistingMonthsWithEarnings(businessId);
        return ResponseEntity.ok(monthEarnings);
    }
}