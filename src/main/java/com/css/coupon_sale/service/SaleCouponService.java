package com.css.coupon_sale.service;

import com.css.coupon_sale.entity.OrderEntity;
import org.springframework.stereotype.Service;

@Service
public interface SaleCouponService {


    boolean insertSaleCoupon(int orderId);
}
