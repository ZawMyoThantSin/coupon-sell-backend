package com.css.coupon_sale.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOwnerRequest {
    private int businessId;
    private Double desiredPercentage;

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public Double getDesiredPercentage() {
        return desiredPercentage;
    }

    public void setDesiredPercentage(Double desiredPercentage) {
        this.desiredPercentage = desiredPercentage;
    }
}
