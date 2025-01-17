package com.css.coupon_sale.dto.response;

import java.util.Date;

public class BusinessCouponSalesResponse {
    private int saleCouponId;
    private int businessId;
    private int soldCount;
    private Date buyDate;
    // Constructor
    public BusinessCouponSalesResponse(int saleCouponId, int businessId, int soldCount,Date buyDate) {
        this.saleCouponId = saleCouponId;
        this.businessId = businessId;
        this.soldCount = soldCount;
        this.buyDate = buyDate;
    }

    public int getSaleCouponId() {
        return saleCouponId;
    }

    public void setSaleCouponId(int saleCouponId) {
        this.saleCouponId = saleCouponId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }
}
