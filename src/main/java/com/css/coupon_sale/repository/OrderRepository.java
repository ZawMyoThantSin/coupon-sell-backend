package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
  List<OrderEntity> findByCouponId(Integer id);
  List<OrderEntity> findByUserId(long id);
  List<OrderEntity> findByPaymentId(Integer id);

}
