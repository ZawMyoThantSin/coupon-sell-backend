package com.css.coupon_sale.dto.response;

import java.util.Date;

public class BusinessPaidHistoryResponse {
    private String businessName;
    private Double adminProfit;
    private Double percentage;
    private Double paidAmount;
    private Date paidAt;

    public BusinessPaidHistoryResponse(String businessName, Double adminProfit, Double percentage, Double paidAmount, Date paidAt) {
        this.businessName = businessName;
        this.adminProfit = adminProfit;
        this.percentage = percentage;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Double getAdminProfit() {
        return adminProfit;
    }

    public void setAdminProfit(Double adminProfit) {
        this.adminProfit = adminProfit;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }
}
