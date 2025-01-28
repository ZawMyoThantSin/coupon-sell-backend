package com.css.coupon_sale.dto.response;

import java.util.Date;

public class BusinessReportResponse {
    private String businessName;
    private String userName;
    private String email;
    private String contactNumber;
    private Date createdAt;


    // Constructor
    public BusinessReportResponse(String businessName, String userName, String email, String contactNumber, Date createdAt) {
        this.businessName = businessName;
        this.userName = userName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.createdAt = createdAt;

    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
