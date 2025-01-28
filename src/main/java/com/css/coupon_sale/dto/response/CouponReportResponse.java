package com.css.coupon_sale.dto.response;

import java.util.Date;

public class CouponReportResponse {
    private String productName;
    private double productDiscount;
    private int quantity;
    private Date expiredDate;
    private double price;
    private String businessName;


    // Constructor
    public CouponReportResponse(String productName, double productDiscount, int quantity, Date expiredDate, double price,String businessName) {
        this.productName = productName;
        this.productDiscount = productDiscount;
        this.quantity = quantity;
        this.expiredDate = expiredDate;
        this.price = price;
        this.businessName=businessName;

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
