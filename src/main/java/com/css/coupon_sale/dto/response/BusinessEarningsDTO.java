package com.css.coupon_sale.dto.response;

public class BusinessEarningsDTO {
    private int businessId;
    private double totalEarnings;

    public BusinessEarningsDTO(int businessId, double totalEarnings) {
        this.businessId = businessId;
        this.totalEarnings = totalEarnings;
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
