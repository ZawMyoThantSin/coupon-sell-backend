package com.css.coupon_sale.dto.response;

import java.util.Date;

public class CouponSalesBusinessResponse2 {
    private Integer businessId;
    private String businessName;
    private Date buyDate;
    private Long soldQuantity;
    private Double totalPrice;
    private String productName;
    // Constructor to initialize the fields
    public CouponSalesBusinessResponse2(Integer businessId, String businessName, Date buyDate, Long soldQuantity, Double totalPrice,String productName) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.buyDate = buyDate;
        this.soldQuantity = soldQuantity;
        this.totalPrice = totalPrice;
        this.productName=productName;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Long getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Long soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
