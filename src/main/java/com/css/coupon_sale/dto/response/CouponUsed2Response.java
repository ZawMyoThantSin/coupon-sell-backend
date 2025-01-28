package com.css.coupon_sale.dto.response;

import java.util.Date;

public class CouponUsed2Response {
    private String userName;
    private String email;
    private Date usedAt;
    private String productName;

    public CouponUsed2Response(String userName, String email, Date usedAt, String productName) {
        this.userName = userName;
        this.email = email;
        this.usedAt = usedAt;
        this.productName = productName;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Date usedAt) {
        this.usedAt = usedAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
