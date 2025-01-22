package com.css.coupon_sale.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PayOwnerResponse {
    private int businessId;
    private Double paidAmount;
    private Double adminShare;
    private Double ownerShare;
    private LocalDateTime paymentDate;

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getAdminShare() {
        return adminShare;
    }

    public void setAdminShare(Double adminShare) {
        this.adminShare = adminShare;
    }

    public Double getOwnerShare() {
        return ownerShare;
    }

    public void setOwnerShare(Double ownerShare) {
        this.ownerShare = ownerShare;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PayOwnerResponse(int businessId, Double paidAmount, Double adminShare, Double ownerShare, LocalDateTime paymentDate) {
        this.businessId = businessId;
        this.paidAmount = paidAmount;
        this.adminShare = adminShare;
        this.ownerShare = ownerShare;
        this.paymentDate = paymentDate;
    }
}
