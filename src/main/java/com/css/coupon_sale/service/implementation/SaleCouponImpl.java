package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.response.PurchaseCoupon;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.repository.OrderRepository;
import com.css.coupon_sale.repository.QrCodeRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
import com.css.coupon_sale.service.SaleCouponService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleCouponImpl implements SaleCouponService {
    @Autowired
    private SaleCouponRepository saleCouponRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @Override
    public boolean insertSaleCoupon(int orderId) throws IOException {
        try {
            // Fetch OrderEntity by orderId
            List<OrderEntity> orderEntityList = orderRepository.findByOrderId(orderId);
            if (orderEntityList.isEmpty()) {
                throw new IllegalArgumentException("Order not found for ID: " + orderId);
            }

            // Loop through each OrderEntity and create corresponding SaleCouponEntities
            for (OrderEntity order : orderEntityList) {
                int quantity = order.getQuantity(); // Get the quantity from the order

                for (int i = 0; i < quantity; i++) { // Loop for each unit of the quantity
                    SaleCouponEntity saleCoupon = new SaleCouponEntity();
                    saleCoupon.setBusiness(order.getCoupon().getProduct().getBusiness());
                    saleCoupon.setUser(order.getUser());
                    saleCoupon.setCoupon(order.getCoupon());
                    saleCoupon.setPayment(order.getPayment());
                    saleCoupon.setQuantity(1); // Set quantity to 1 for each row
                    saleCoupon.setStatus(order.getStatus());
                    saleCoupon.setTotalPrice(order.getCoupon().getPrice()); // Assuming totalPrice = price of one coupon
                    saleCoupon.setBuyDate(order.getCreatedAt());
                    saleCoupon.setExpiredDate(order.getCoupon().getExpiredDate()); // Example expiration logic


                    // Save SaleCouponEntity to the database
                    saleCouponRepository.save(saleCoupon);
                    // Generate a unique code (UUID) for each coupon
                    String uniqueCode = UUID.randomUUID().toString();
                    System.out.println("Generated Unique Code for Sale Coupon: " + uniqueCode);


                }
            }
            return true; // Return true if processing is successful
        } catch (Exception e) {
            throw new RuntimeException("Error inserting Sale Coupons: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PurchaseCoupon> getAllCouponsBYUserId(Long userId) {
        List<SaleCouponEntity> saleCouponEntities=saleCouponRepository.findByUser_Id(userId);
        List<PurchaseCoupon> response=new ArrayList<>();
        for (SaleCouponEntity coupon : saleCouponEntities) {
            PurchaseCoupon purchaseCoupon =new PurchaseCoupon();
            purchaseCoupon.setSaleCouponId(coupon.getId());
            purchaseCoupon.setStatus(coupon.getStatus());
            purchaseCoupon.setDiscount(coupon.getCoupon().getProduct().getDiscount());
            purchaseCoupon.setPrice(coupon.getTotalPrice());
            purchaseCoupon.setExpiryDate(coupon.getExpiredDate());
            purchaseCoupon.setProductName(coupon.getCoupon().getProduct().getName());
            purchaseCoupon.setImageUrl(coupon.getCoupon().getProduct().getImagePath());
            response.add(purchaseCoupon);

        }

        return response;
    }


}
