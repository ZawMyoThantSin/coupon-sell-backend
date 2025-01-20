package com.css.coupon_sale.dto.response;

import java.util.Date;

public class CouponUsageResoponse {
    private String userName;
    private String email;
    private Date usedAt;
    private String productName;
    private String businessName;
    public CouponUsageResoponse(String userName, String email, Date usedAt, String productName,String businessName) {
        this.userName = userName;
        this.email = email;
        this.usedAt = usedAt;
        this.productName = productName;
        this.businessName=businessName;
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
