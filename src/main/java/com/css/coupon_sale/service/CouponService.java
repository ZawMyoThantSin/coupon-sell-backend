package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.BusinessCouponSalesResponse;
import com.css.coupon_sale.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse getCouponById(Integer id);
    List<CouponResponse> getAllCouponlist();
    CouponResponse updateCoupon(Integer id, CouponRequest requestDTO);
    CouponResponse saveCoupon(CouponRequest requestDTO);
    List<CouponResponse> showByBusinessId(Integer id);
    List<BusinessCouponSalesResponse> getSoldCouponCountByBusiness(Integer businessId);
}
