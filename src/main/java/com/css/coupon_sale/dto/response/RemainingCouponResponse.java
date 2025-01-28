package com.css.coupon_sale.dto.response;

import java.util.Date;

public class RemainingCouponResponse {
    private String email;
    private Date buyDate;
    private Date expiredDate;
    private double totalPrice;
    private String productName;

    public RemainingCouponResponse(String email, Date buyDate, Date expiredDate, double totalPrice, String productName) {
        this.email = email;
        this.buyDate = buyDate;
        this.expiredDate = expiredDate;
        this.totalPrice = totalPrice;
        this.productName = productName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
