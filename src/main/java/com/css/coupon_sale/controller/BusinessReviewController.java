package com.css.coupon_sale.controller;


import com.css.coupon_sale.dto.request.BusinessReviewRequest;
import com.css.coupon_sale.dto.response.BusinessReviewResponse;
import com.css.coupon_sale.service.BusinessReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business_review")
public class BusinessReviewController {

    @Autowired
    private final BusinessReviewService businessReviewService;

    public BusinessReviewController(BusinessReviewService businessReviewService) {
        this.businessReviewService = businessReviewService;
    }



    @PostMapping
    public ResponseEntity<?> ratebusiness(@RequestBody ReviewData reviewData) {

        System.out.println("Name:"+ reviewData.message);
        System.out.println("Name :" + reviewData.user_id);
        try {
            BusinessReviewRequest dto = new BusinessReviewRequest();

            dto.setCount(reviewData.count);
            dto.setMessage(reviewData.message);
            dto.setBusiness_id(reviewData.business_id);
            dto.setUser_id(reviewData.user_id);


            BusinessReviewResponse responseDto = businessReviewService.rateBusiness(dto);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rating business: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BusinessReviewResponse>> getAllBusinesses() {
        List<BusinessReviewResponse> rating = businessReviewService.getAllRatingList();
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/overview/{business_id}")
    public ResponseEntity<Double> getOverviewCount(@PathVariable String business_id) {
        try {
            business_id = business_id.trim();  // Remove leading/trailing spaces or newlines
            int businessId = Integer.parseInt(business_id);
            double overviewCount = businessReviewService.calculateOverviewCount(businessId);
            return ResponseEntity.ok(overviewCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    public record ReviewData(Integer business_id, Long user_id, int count, String message ){}

    @GetMapping("/has-rated/{userId}/{businessId}")
    public ResponseEntity<Boolean> hasUserRated(@PathVariable("userId")Long userId, @PathVariable("businessId")Integer businessId) {
        boolean hasRated = businessReviewService.hasUserRated(businessId, userId);
        return ResponseEntity.ok(hasRated);
    }


    @GetMapping("/business/{id}")
    public ResponseEntity<BusinessReviewResponse> getRatingByBusinessId(@PathVariable int id) {
        try {
            BusinessReviewResponse response = businessReviewService.getByBusinessId(id);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            System.out.println("Error"+ e.getMessage());
            return ResponseEntity.ok(new BusinessReviewResponse());
        }
    }

}
