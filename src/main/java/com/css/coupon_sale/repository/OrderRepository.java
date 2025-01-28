package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.ProductEntity;
import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
  List<OrderEntity> findByCouponId(Integer id);
  List<OrderEntity> findByUserId(long id);
  List<OrderEntity> findByPaymentId(Integer id);
  List<OrderEntity> findByOrderId(int orderId);

  @Query(value = "SELECT COUNT(*) > 0 FROM userorder WHERE order_id = :orderId", nativeQuery = true)
  boolean existsByOrderId(@Param("orderId") int orderId);

  @Query(value = "SELECT COUNT(*) FROM userorder WHERE status = :status", nativeQuery = true)
  int countByStatus(@Param("status") int status);
//  @Query(value = "SELECT * FROM userorder WHERE order_id = :orderId LIMIT 1", nativeQuery = true)
//  Optional<OrderEntity> findByOrderId(@Param("orderId") int orderId);

  @Query("SELECT o FROM OrderEntity o WHERE o.coupon.product.business.id = :businessId AND o.status = 1")
  List<OrderEntity> findByBusinessId(@Param("businessId") int businessId);

  @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.status = 0")
  long countTotalOrdersWithStatusZero();

  @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.status IN (1, 2)")
  long countCompletedOrders();

  @Query("SELECT COUNT(o) FROM OrderEntity o WHERE DATE(o.createdAt) = CURRENT_DATE AND o.status = 0")
  long countOrdersForToday();

  @Query("SELECT p.name AS productName, p.price AS price, p.discount AS discount, " +
          "c.price AS totalPrice, SUM(o.quantity) AS quantity " +
          "FROM OrderEntity o " +
          "JOIN o.coupon c " +
          "JOIN c.product p " +
          "WHERE p.business.id = :businessId " +
          "AND o.status = 1 " +
          "GROUP BY p.id, p.name, p.price, c.price, p.discount " +
          "ORDER BY quantity DESC")
  List<Object[]> findBestSellingProducts(@Param("businessId") int businessId);

}
