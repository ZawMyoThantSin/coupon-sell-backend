package com.css.coupon_sale.dto.response;

public class BusinessEarningsDTO {
    private int businessId;
    private double totalEarnings;
    private String businessName;

    public BusinessEarningsDTO(int businessId, double totalEarnings, String businessName) {
        this.businessId = businessId;
        this.totalEarnings = totalEarnings;
        this.businessName = businessName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
}
