package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.dto.response.CouponUsedResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.CouponService;
import com.css.coupon_sale.service.ProductService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/view/{id}")
    public ResponseEntity<Boolean> increaseViewCount(@PathVariable Integer id) {
        couponService.increaseViewCount(id);
        return ResponseEntity.ok(true);
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

    @GetMapping("/weeklyRreport/{id}")
    public ResponseEntity<byte[]> saleCouponReportForWeekly(@PathVariable("id") Integer businessId, @RequestParam String reportType) {
        try {

            // Validate inputs
            if (businessId == null || reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            byte[] reportBytes = couponService.saleCouponReportForWeekly(businessId,reportType);

            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=product_list.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=product_list.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0,pre-check=0");

            return ResponseEntity.ok().headers(headers).body(reportBytes);
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }




    @GetMapping("/monthlyReport/{id}")
    public ResponseEntity<byte[]> saleCouponReportForMonthly(@PathVariable("id") Integer businessId, @RequestParam String reportType) {
        try {

            // Validate inputs
            if (businessId == null || reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            byte[] reportBytes = couponService.saleCouponReportForMonthly(businessId,reportType);

            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=product_list.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=product_list.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0,pre-check=0");

            return ResponseEntity.ok().headers(headers).body(reportBytes);
        } catch (JRException  e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }
    @GetMapping("/usages/{shopId}")
    public ResponseEntity<List<CouponUsedResponse>> getAllCouponUsages(@PathVariable Integer shopId) {
        List<CouponUsedResponse> usages = couponService.getAllCouponUsages(shopId);
        return ResponseEntity.ok(usages);
    }
}
