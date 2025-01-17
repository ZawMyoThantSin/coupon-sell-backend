package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.BusinessCouponSalesResponse;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.dto.response.CouponUsedResponse;
import net.sf.jasperreports.engine.JRException;

import java.util.List;
import java.util.Map;

public interface CouponService {
    CouponResponse getCouponById(Integer id);
    List<CouponResponse> getAllCouponlist();
    CouponResponse updateCoupon(Integer id, CouponRequest requestDTO);
    CouponResponse saveCoupon(CouponRequest requestDTO);
    List<CouponResponse> showByBusinessId(Integer id);
    List<BusinessCouponSalesResponse> getSoldCouponCountByBusiness(Integer businessId);
    void increaseViewCount(Integer couponId);
    byte[] saleCouponReportForWeekly(Integer businessId, String reportType) throws JRException;

    byte[] saleCouponReportForMonthly(Integer businessId, String reportType) throws JRException;

    List<CouponUsedResponse> getAllCouponUsages(Integer shopId);
}
