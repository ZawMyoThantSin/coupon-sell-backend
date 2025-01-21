package com.css.coupon_sale.service;

public interface EmailService {

    void sendOtp(String toEmail, String otp);

    String generateOtp();

    void saveOtp(String email, String otp);

    String  validateOtp(String email, String otp);

    String validateEmail(String email, String ipAddress);
}
