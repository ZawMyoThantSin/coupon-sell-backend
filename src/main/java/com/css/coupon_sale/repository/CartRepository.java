package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity,Integer> {
    Optional<CartEntity> findByCoupon_IdAndUser_Id(int couponId, Long userId);
    List<CartEntity> findByUser_Id(Long id);
}
