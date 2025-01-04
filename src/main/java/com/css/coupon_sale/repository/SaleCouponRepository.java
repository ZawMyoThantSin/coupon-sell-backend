package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleCouponRepository extends JpaRepository<SaleCouponEntity , Integer> {
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId")
    List<OrderEntity> findByOrderId(@Param("orderId") int orderId);

    List<SaleCouponEntity> findByUser_Id(Long userId);


}
