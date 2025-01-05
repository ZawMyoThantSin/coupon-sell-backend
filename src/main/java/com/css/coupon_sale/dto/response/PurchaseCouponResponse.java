package com.css.coupon_sale.dto.response;

import java.time.LocalDateTime;

public class PurchaseCouponResponse {
    private Integer saleCouponId;
    private String productName;
    private float discount;
    private LocalDateTime expiryDate;
    private double price;
    private String imageUrl;
    private int status;

    public Integer getSaleCouponId() {
        return saleCouponId;
    }

    public void setSaleCouponId(Integer saleCouponId) {
        this.saleCouponId = saleCouponId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
