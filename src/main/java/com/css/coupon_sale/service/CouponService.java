package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.*;
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

    List<CouponUsedResponse> getAllCouponUsages(Integer businessId);

    void softDeleteCoupon(Integer id);

    byte[] generateCouponUsageReportweekly(Integer businessId, String reportType) throws JRException;

    byte[] generateCouponUsageReportmonthly(Integer businessId, String reportType) throws JRException;

    byte[] generateCouponReport(Integer businessId, String reportType) throws JRException;

    List<CouponUsed2Response> getAllCoupon(Integer businessId);

    byte[] generateCouponUsageReport(List<CouponUsed2Response> couponUsages, String reportType) throws JRException;

    List<CouponSalesBusinessResponse2> getAllCouponSales(Integer businessId);

    byte[] generateCouponSalesReport(List<CouponSalesBusinessResponse2> couponSales, String reportType) throws JRException;

    byte[] generateRemainingCouponReport(Integer businessId, String reportType) throws JRException;

    byte[] generateExpiredCouponDataReport(Integer businessId, String reportType) throws JRException;

    byte[] generateBestSellingProductReport(Integer businessId, String reportType) throws JRException;
}
