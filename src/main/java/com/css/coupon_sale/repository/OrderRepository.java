package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
  List<OrderEntity> findByCouponId(Integer id);
  List<OrderEntity> findByUserId(long id);
  List<OrderEntity> findByPaymentId(Integer id);

  @Query(value = "SELECT COUNT(*) > 0 FROM userorder WHERE order_id = :orderId", nativeQuery = true)
  boolean existsByOrderId(@Param("orderId") int orderId);

  @Query(value = "SELECT * FROM userorder WHERE order_id = :orderId LIMIT 1", nativeQuery = true)
  Optional<OrderEntity> findByOrderId(@Param("orderId") int orderId);

}
