package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.entity.OtpEntity;
import com.css.coupon_sale.repository.OtpRepository;
import com.css.coupon_sale.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Override
    public void sendOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true allows file attachment
            helper.setTo(toEmail);
            helper.setSubject(" Your OTP Code | From Team Ichigo");

            // Read and modify the HTML template
            String htmlContent;
            try (var inputStream = Objects.requireNonNull(EmailServiceImpl.class.getResourceAsStream("/templates/otp-mail.html"))) {
                htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // Replace the placeholder in the HTML with the generated OTP
            htmlContent = htmlContent.replace("${otp}", otp);

            // Set the email body with the modified HTML
            helper.setText(htmlContent, true);

            // Send the email
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public void saveOtp(String email, String otp) {
        OtpEntity otp1 = otpRepository.findByEmail(email);
        if(otp1 != null){
            otp1.setEmail(email);
            otp1.setOtp(otp);
            otp1.setExpirationTime(LocalDateTime.now().plusMinutes(1)); // 1-minute validity
            otpRepository.save(otp1);
        }else {
            OtpEntity otpEntity = new OtpEntity();
            otpEntity.setEmail(email);
            otpEntity.setOtp(otp);
            otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(1)); // 1-minute validity
            otpRepository.save(otpEntity);
        }
    }

    @Override
    public String  validateOtp(String email, String otp) {
        OtpEntity otpEntity = otpRepository.findByEmail(email);

        if (otpEntity == null || !otpEntity.getOtp().equals(otp)) {
            return "INV"; // Invalid OTP
        }

        if (otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            return "EXP"; // OTP expired
        }

        // Clear the OTP after successful validation
        otpRepository.delete(otpEntity);
        return "SUCCESS";
    }
}
