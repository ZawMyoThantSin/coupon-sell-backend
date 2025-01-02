package com.css.coupon_sale.controller;

import com.css.coupon_sale.entity.PaymentEntity;
import com.css.coupon_sale.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payment/all")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        try {
            // Fetch all payments from the service layer
            List<PaymentEntity> payments = paymentService.getAllPayment();

            // If no payments are found, return a no content response
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
            }

            // Convert each Payment entity into a PaymentResponse record
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(payment -> new PaymentResponse(
                            payment.getId(),
                            payment.getPaymentType(),
                            payment.getAccountName(),
                            payment.getAccountNumber(),
                            payment.getQrImage() != null ? payment.getQrImage(): null))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(paymentResponses);
        } catch (Exception e) {
            // Handle any exceptions and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @PostMapping("/payment/create")
    public ResponseEntity<Boolean> handlePayment(
            @RequestParam("payment_type") String paymentType,
            @RequestParam("accountName") String accountName,
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "qr_image", required = false) MultipartFile qrImage) throws IOException {
        System.out.println("Type"+ paymentType);
        System.out.println("Name"+ accountName);
        System.out.println("num"+ accountNumber);
        System.out.println("r"+ qrImage.getOriginalFilename());
           boolean status =  paymentService.createPayment(paymentType,accountName,accountNumber,qrImage);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/payment/update/{id}")
    public ResponseEntity<Boolean> updatePayment(
            @PathVariable("id") int paymentId,
            @RequestParam("payment_type") String paymentType,
            @RequestParam("accountName") String accountName,
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "qrImage", required = false) MultipartFile qrImage) throws IOException {

        boolean isUpdated = paymentService.updatePayment(paymentId,paymentType,accountName,accountNumber,qrImage);
        return ResponseEntity.ok(isUpdated);
    }

    public record PaymentResponse(int id, String paymentType, String accountName, String  accountNumber, String qrImage) {
        // Constructor and getters are automatically provided by the record
    }

}
