package com.css.coupon_sale.controller;


import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.request.TransferRequest;
import com.css.coupon_sale.dto.response.TransferResponse;
import com.css.coupon_sale.repository.FriendshipRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.FriendshipService;
import com.css.coupon_sale.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;
    private final CustomWebSocketHandler webSocketHandler;

    @Autowired
    public TransferController(TransferService transferService, CustomWebSocketHandler webSocketHandler) {
        this.transferService = transferService;
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transferCoupon(@RequestBody TransferRequest requestDTO) {
        try {
            TransferResponse response = transferService.transferCoupon(requestDTO);
            System.out.println("Accepter ID : " + requestDTO.getAccepterId());

            String message = "COUPON_TRANSFER_TRANSFERRED";
            webSocketHandler.sendToUser(requestDTO.getAccepterId(), message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{transferId}/accept")
    public ResponseEntity<TransferResponse> acceptTransfer(@PathVariable int transferId) {
        try {
            TransferResponse response = transferService.acceptTransfer(transferId);
            String message = "COUPON_TRANSFER_ACCEPTED";
            webSocketHandler.sendToUser(response.getSenderId(), message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{transferId}/deny")
    public ResponseEntity<TransferResponse> denyTransfer(@PathVariable int transferId) {
        try {
            TransferResponse response = transferService.denyTransfer(transferId);
            String message = "COUPON_TRANSFER_DENIED";
            webSocketHandler.sendToUser(response.getSenderId(), message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransferResponse>> getTransferHistory(@PathVariable Long userId) {
        List<TransferResponse> history = transferService.getTransferHistory(userId);
        return ResponseEntity.ok(history);
    }
    @GetMapping("/couponAcepter/{accepterId}")
    public ResponseEntity<?> getAcceptTransfersCoupern(@PathVariable("accepterId") Long accepterId) {
        // Check if the accepterId exists
        List<TransferResponse> transfer = transferService.getCouponsForAcceptor(accepterId);

        if (transfer == null || transfer.isEmpty()) {
            return ResponseEntity.status(404).body("No coupons found for the given accepter ID.");
        }
        return ResponseEntity.ok(transfer);
    }

}
