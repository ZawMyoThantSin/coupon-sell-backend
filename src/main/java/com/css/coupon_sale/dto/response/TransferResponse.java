package com.css.coupon_sale.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferResponse {

    private int transferId;
    private Long senderId;
    private Long accepterId;
    private int saleCouponId;
    private int status;
    private LocalDateTime transferAt;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

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

    public LocalDateTime getTransferAt() {
        return transferAt;
    }

    public void setTransferAt(LocalDateTime transferAt) {
        this.transferAt = transferAt;
    }
}
