package com.css.coupon_sale.dto.response;

import java.util.Date;

public class ExpiredCouponDataResponse {
    private Date expiredDate;
    private String productName;  // Change field name to productName
    private Double price;
    private Integer totalQuantity;
    private Integer soldOutQuantity;

    // Constructor, getters, and setters
    public ExpiredCouponDataResponse(Date expiredDate, String productName, Double price, Integer totalQuantity, Integer soldOutQuantity) {
        this.expiredDate = expiredDate;
        this.productName = productName;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.soldOutQuantity = soldOutQuantity;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getSoldOutQuantity() {
        return soldOutQuantity;
    }

    public void setSoldOutQuantity(Integer soldOutQuantity) {
        this.soldOutQuantity = soldOutQuantity;
    }
}
