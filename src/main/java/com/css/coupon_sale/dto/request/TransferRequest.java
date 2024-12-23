package com.css.coupon_sale.dto.request;

import lombok.Data;

@Data
public class TransferRequest {
    private Long senderId;
    private Long accepterId;
    private int saleCouponId;
    private int status;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getAccepterId() {
        return accepterId;
    }

    public void setAccepterId(Long accepterId) {
        this.accepterId = accepterId;
    }

    public int getSaleCouponId() {
        return saleCouponId;
    }

    public void setSaleCouponId(int saleCouponId) {
        this.saleCouponId = saleCouponId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
