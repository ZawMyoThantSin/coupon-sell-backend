package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.BusinessRequest;
import com.css.coupon_sale.dto.request.BusinessReviewRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.BusinessReviewResponse;

import java.io.IOException;
import java.util.List;

public interface BusinessReviewService {
    //  String rateBusiness(Long user_id, int business_id, int count, String message);
    BusinessReviewResponse rateBusiness(BusinessReviewRequest requestDTO) throws IOException;

    double calculateOverviewCount(int business_id);

    boolean hasUserRated(int business_id, Long user_id);

    BusinessReviewResponse getByBusinessId(int id);

    List<BusinessReviewResponse> getAllRatingsByBusinessId(int business_id);
}