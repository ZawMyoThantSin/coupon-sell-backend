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

    @Query("SELECT SUM(sc.totalPrice) FROM SaleCouponEntity sc WHERE sc.business.id = :businessId")
    Double getTotalIncomeByBusinessId(@Param("businessId") int businessId);

    @Query("SELECT s.business.id AS businessId,s.business.name AS businessName, SUM(s.totalPrice) AS totalEarnings " +
            "FROM SaleCouponEntity s " +
            "GROUP BY s.business.id")
    List<Object[]> groupTotalEarningsByBusinessId();

    @Query("SELECT s.business.id AS businessId,s.business.name AS businessName, SUM(s.totalPrice) AS totalEarnings " +
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

    @Query("SELECT YEAR(s.buyDate), MONTH(s.buyDate), SUM(s.totalPrice) " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "GROUP BY YEAR(s.buyDate), MONTH(s.buyDate) " +
            "ORDER BY YEAR(s.buyDate) DESC, MONTH(s.buyDate) DESC")
    List<Object[]> findMonthlyEarningsByBusinessId(@Param("businessId") int businessId);

    @Query("SELECT SUM(s.totalPrice) AS yearlyEarnings " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "AND s.buyDate >= :startOfYear " +
            "AND s.buyDate < :startOfNextYear")
    Double findYearlyEarningsByBusinessIdAndYear(
            @Param("businessId") int businessId,
            @Param("startOfYear") LocalDateTime startOfYear,
            @Param("startOfNextYear") LocalDateTime startOfNextYear);

    @Query("SELECT s.business.id, s.business.name, s.buyDate, SUM(s.quantity), SUM(s.totalPrice), s.coupon.product.name " +
            "FROM SaleCouponEntity s " +
            "WHERE s.business.id = :businessId " +
            "GROUP BY s.business.id, s.business.name, s.buyDate, s.coupon.id, s.coupon.product.name")
    List<Object[]> findAllSaleCoupon(@Param("businessId") Integer businessId);


    @Query("SELECT sc.user.email AS email, sc.buyDate AS buyDate, sc.expiredDate AS expiredDate, sc.totalPrice AS totalPrice, p.name AS productName " +
            "FROM SaleCouponEntity sc " +
            "JOIN sc.coupon c " +
            "JOIN c.product p " +
            "JOIN sc.user u " +
            "WHERE sc.business.id = :businessId " +
            "AND sc.status != 1")
    List<Object[]> findRemainingCoupons(@Param("businessId") int businessId);

}
