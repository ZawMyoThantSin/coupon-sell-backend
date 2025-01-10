package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.PurchaseCouponResponse;
import com.css.coupon_sale.dto.response.TransferResponse;
import com.css.coupon_sale.entity.TransferEntity;

import java.util.List;

public interface TransferService {

    TransferResponse transferCoupon(TransferRequest requestDTO);

    TransferResponse acceptTransfer(int transferId);

    TransferResponse denyTransfer(int transferId);

    List<TransferResponse> getTransferHistory(Long userId);

    List<TransferResponse> getCouponsForAccepter(Long accepterId);

    boolean transferCoupon(int saleCouponId, Long acceptorId);



}
