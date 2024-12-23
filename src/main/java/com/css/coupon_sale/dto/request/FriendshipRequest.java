package com.css.coupon_sale.dto.request;

import lombok.Data;

@Data
public class FriendshipRequest {
    private int senderId;
    private int accepterId;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getAccepterId() {
        return accepterId;
    }

    public void setAccepterId(int accepterId) {
        this.accepterId = accepterId;
    }
}