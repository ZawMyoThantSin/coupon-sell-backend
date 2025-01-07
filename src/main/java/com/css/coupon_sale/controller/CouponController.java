package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.CouponService;
import com.css.coupon_sale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductService pservice;


    @GetMapping("/calculate/{id}")
    public ResponseEntity<Double> calculateCouponPrice(@PathVariable("id") Integer ProductId) {
        try {
            ProductResponse product = pservice.showProductbyId(ProductId);
//            double discountedPrice = CouponService.calculateDiscountedPrice(productId, couponCode);
            double originPrice = product.getPrice();
            float discountPrice = product.getDiscount();

//            double rate = discountPrice / 100.0;
            double f = originPrice * (discountPrice / 100.0);
            System.out.println(f);
            double FinalPrice = originPrice - f;
            System.out.println(FinalPrice);
            return ResponseEntity.ok(FinalPrice);
        } catch (IllegalArgumentException e) {
            System.out.println("ERRor in Calculate"+ e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest requestDTO) {
        // Call service with the BusinessRequest DTO
        CouponResponse savedCoupon = couponService.saveCoupon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Integer id) {
        CouponResponse business = couponService.getCouponById(id);
        return ResponseEntity.ok(business);
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupon() {
        List<CouponResponse> businesses = couponService.getAllCouponlist();
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Integer id, @RequestBody CouponRequest
            requestDTO) {
        CouponResponse updatedCoupon = couponService.updateCoupon(id, requestDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    @GetMapping("/b/{id}")
    public ResponseEntity<List<CouponResponse>> getByCoupon(@PathVariable("id")Integer id){
        List<CouponResponse> responses = couponService.showByBusinessId(id);
        if(responses != null){
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.notFound().build();
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteCoupon(@PathVariable Integer id) {
//        CouponResponse b = CouponService.softDeleteCoupon(id);
//        return ResponseEntity.ok("Business deleted successfully");
//    }
}
