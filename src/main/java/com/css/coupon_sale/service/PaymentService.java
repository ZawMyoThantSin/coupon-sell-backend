package com.css.coupon_sale.service;

import com.css.coupon_sale.entity.PaymentEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PaymentService {

    boolean createPayment(String paymentType, String accountName, String accountNumber, MultipartFile qrImage) throws IOException;

    boolean updatePayment(int id, String paymentType, String accountName, String accountNumber, MultipartFile qrImage) throws IOException;

    List<PaymentEntity> getAllPayment();
}
