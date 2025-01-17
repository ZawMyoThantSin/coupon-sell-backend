package com.css.coupon_sale.dto.response;

import java.time.LocalDateTime;

public class CouponUsedResponse {
    private String userName;
    private String email;
    private LocalDateTime usedAt;
    private String productName;

    public CouponUsedResponse(String userName, String email, LocalDateTime usedAt, String productName) {
        this.userName = userName;
        this.email = email;
        this.usedAt = usedAt;
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
