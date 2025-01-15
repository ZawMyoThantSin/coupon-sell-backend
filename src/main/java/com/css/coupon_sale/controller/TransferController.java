package com.css.coupon_sale.controller;


import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.TransferResponse;
import com.css.coupon_sale.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> transferCoupon(@RequestBody TransferRequest requestDTO) {
        TransferResponse response = transferService.transferCoupon(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transferId}/accept")
    public ResponseEntity<TransferResponse> acceptTransfer(@PathVariable int transferId) {
        TransferResponse response = transferService.acceptTransfer(transferId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transferId}/deny")
    public ResponseEntity<TransferResponse> denyTransfer(@PathVariable int transferId) {
        TransferResponse response = transferService.denyTransfer(transferId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransferResponse>> getTransferHistory(@PathVariable Long userId) {
        List<TransferResponse> history = transferService.getTransferHistory(userId);
        return ResponseEntity.ok(history);
    }
    @GetMapping("/couponAcepter/{accepterId}")
    public ResponseEntity<?> getAcceptTransfersCoupern(@PathVariable("accepterId") Long accepterId) {
        // Check if the accepterId exists
        List<TransferResponse> transfer = transferService.getCouponsForAccepter(accepterId);

        if (transfer == null || transfer.isEmpty()) {
            return ResponseEntity.status(404).body("No coupons found for the given accepter ID.");
        }
        return ResponseEntity.ok(transfer);
    }
    @GetMapping("/couponSenderv/{senderId}")
    public ResponseEntity<?> getSendTransfersCoupern(@PathVariable("senderId") Long senderId) {
        // Check if the accepterId exists
        List<TransferResponse> transfer = transferService.getCouponsForSender(senderId);

        if (transfer == null || transfer.isEmpty()) {
            return ResponseEntity.status(404).body("No coupons found for the given accepter ID.");
        }
        return ResponseEntity.ok(transfer);
    }


}
