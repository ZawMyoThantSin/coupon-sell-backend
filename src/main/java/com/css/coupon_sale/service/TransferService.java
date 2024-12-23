package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.TransferResponse;

import java.util.List;

public interface TransferService {

    TransferResponse transferCoupon(TransferRequest requestDTO);

    TransferResponse acceptTransfer(int transferId);

    TransferResponse denyTransfer(int transferId);

    List<TransferResponse> getTransferHistory(Long userId);

}
