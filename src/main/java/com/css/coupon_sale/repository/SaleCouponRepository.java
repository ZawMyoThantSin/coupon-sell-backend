package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleCouponRepository extends JpaRepository<SaleCouponEntity , Integer> {
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId")
    List<OrderEntity> findByOrderId(@Param("orderId") int orderId);

    List<SaleCouponEntity> findByUser_Id(Long userId);

    @Query("SELECT s.business.id AS businessId, SUM(s.totalPrice) AS totalEarnings " +
            "FROM SaleCouponEntity s " +
            "GROUP BY s.business.id")
    List<Object[]> groupTotalEarningsByBusinessId();

    @Query("SELECT s.business.id AS businessId, SUM(s.totalPrice) AS totalEarnings " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "GROUP BY s.business.id")
    List<Object[]> findTotalEarningsByBusinessId(@Param("businessId") int businessId);

    @Query("SELECT SUM(s.totalPrice) AS monthlyEarnings " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "AND s.buyDate >= :startOfMonth " +
            "AND s.buyDate < :startOfNextMonth")
    Double findMonthlyEarningsByBusinessIdAndMonth(
            @Param("businessId") int businessId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth);

    @Query("SELECT SUM(s.totalPrice) AS yearlyEarnings " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "AND s.buyDate >= :startOfYear " +
            "AND s.buyDate < :startOfNextYear")
    Double findYearlyEarningsByBusinessIdAndYear(
            @Param("businessId") int businessId,
            @Param("startOfYear") LocalDateTime startOfYear,
            @Param("startOfNextYear") LocalDateTime startOfNextYear);


}
