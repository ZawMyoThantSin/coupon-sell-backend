package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.entity.PaymentEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.PaymentRepository;
import com.css.coupon_sale.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;


    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean createPayment(String paymentType, String accountName, String accountNumber, MultipartFile qrImage) throws IOException {
        PaymentEntity paymentEntity= new PaymentEntity();
        paymentEntity.setPaymentType(paymentType);
        paymentEntity.setAccountName(accountName);
        paymentEntity.setAccountNumber(Integer.parseInt(accountNumber));
        paymentEntity.setCreatedAt(LocalDateTime.now());
        // Save the uploaded image
        if (qrImage != null && !qrImage.isEmpty()) {
//            System.out.println("IMage is exist");
            String fileName = System.currentTimeMillis() + "_" + qrImage.getOriginalFilename();
            Path filePath = Paths.get(uploadDir+"/qr_images", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, qrImage.getBytes());
            paymentEntity.setQrImage(fileName); // Save file name/path
        }
        try {
            PaymentEntity saved = paymentRepository.save(paymentEntity);
        }catch (Exception e){
            System.out.println("ERROR"+ e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updatePayment(int id, String paymentType, String accountName, String accountNumber, MultipartFile qrImage) throws IOException {
        PaymentEntity existing = paymentRepository.findById(id).orElseThrow(
                ()->new AppException("Not Found with that id"+id, HttpStatus.NOT_FOUND));
        if(existing !=null){
            existing.setPaymentType(paymentType);
            existing.setAccountName(accountName);
            existing.setAccountNumber(Integer.parseInt(accountNumber));
            existing.setUpdatedAt(LocalDateTime.now());
            if (qrImage != null && !qrImage.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + qrImage.getOriginalFilename();
                Path filePath = Paths.get(uploadDir+"/qr_images", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, qrImage.getBytes());
                existing.setQrImage(fileName); // Save file name/path
            }

            try{
                PaymentEntity saved = paymentRepository.save(existing);
                return true;
            }catch (Exception e){
                System.out.println("Error "+ e.getMessage());
            }
        }
        return false;
    }

    @Override
    public List<PaymentEntity> getAllPayment() {
        return paymentRepository.findAll();
    }
}
