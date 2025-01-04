package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.response.PurchaseCoupon;
import com.css.coupon_sale.entity.SaleCouponEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SaleCouponService {


    boolean insertSaleCoupon(int orderId);

    List<PurchaseCoupon> getAllCouponsBYUserId(Long userId);
}
