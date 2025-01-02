package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.repository.SaleCouponRepository;
import com.css.coupon_sale.service.SaleCouponService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class SaleCouponImpl implements SaleCouponService {
    @Autowired
    private SaleCouponRepository saleCouponRepository;
    @Override
    public boolean saveSaleCouponFromOrder(OrderEntity orderEntity) {
        try {
            SaleCouponEntity saleCoupon = new SaleCouponEntity();
            saleCoupon.setCoupon(orderEntity.getCoupon());
            saleCoupon.setUser(orderEntity.getUser());
            saleCoupon.setBusiness(orderEntity.getCoupon().getProduct().getBusiness().getId());
            saleCoupon.setPayment(orderEntity.getPayment());
            saleCoupon.setQuantity(orderEntity.getQuantity());
            saleCoupon.setTotalPrice(orderEntity.getTotalPrice());
            saleCoupon.setBuyDate(LocalDateTime.now());
            saleCoupon.setExpiredDate(orderEntity.getCoupon().getExpiredDate()); // Assuming coupon has an expiration date
            saleCoupon.setStatus(1); // Example status, update as per requirements

            saleCouponRepository.save(saleCoupon);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
