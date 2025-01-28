package com.css.coupon_sale.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PaidHistoryReportResponse {
    private String businessName;
    private Date paymentDate;
    private Double paidAmount;
    private Double desiredPercentage;
    private Double adminProfit;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getDesiredPercentage() {
        return desiredPercentage;
    }

    public void setDesiredPercentage(Double desiredPercentage) {
        this.desiredPercentage = desiredPercentage;
    }

    public Double getAdminProfit() {
        return adminProfit;
    }

    public void setAdminProfit(Double adminProfit) {
        this.adminProfit = adminProfit;
    }

    public PaidHistoryReportResponse(String businessName, Date paymentDate, Double paidAmount, Double desiredPercentage, Double adminProfit) {
        this.businessName = businessName;
        this.paymentDate = paymentDate;
        this.paidAmount = paidAmount;
        this.desiredPercentage = desiredPercentage;
        this.adminProfit = adminProfit;
    }
}