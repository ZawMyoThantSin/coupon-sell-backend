package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity,Integer> {
    List<CouponEntity> findByProduct_Business_Id(int businessId);
    List<CouponEntity> findByProduct_BusinessId(int businessId);

    @Query("SELECT sc.id as saleCouponId, sc.business.id as businessId, SUM(sc.quantity) as soldCount, DATE(sc.buyDate) as buyDate " +
            "FROM SaleCouponEntity sc " +
            "WHERE sc.business.id = :businessId " +
            "GROUP BY sc.id, sc.business.id, DATE(sc.buyDate)")
    List<Object[]> getSoldCouponCountByBusiness(@Param("businessId") Integer businessId);

    @Query("SELECT b.id AS businessId, b.name AS businessName, " +
            "DATE(sc.buyDate) AS buyDate, c.id AS couponId, p.name AS productName, " +
            "SUM(sc.quantity) AS soldQuantity, SUM(sc.totalPrice) AS totalPrice " +
            "FROM SaleCouponEntity sc " +
            "JOIN sc.business b " +
            "JOIN sc.coupon c " +
            "JOIN c.product p " +
            "WHERE sc.business.id = :bid " +
            "GROUP BY b.id, b.name, DATE(sc.buyDate), c.id")
    List<Object[]> saleCouponReport(@Param("bid") Integer businessId);



    @Query("SELECT p.name as productName, p.discount as productDiscount, " +
            "c.quantity as quantity, c.expiredDate as expiredDate, c.price as price, " +
            "p.business.name as businessName " +
            "FROM CouponEntity c " +
            "JOIN ProductEntity p ON c.product.id = p.id " +
            "WHERE p.business.id = :businessId")
    List<Object[]> getCouponReport(@Param("businessId") Integer businessId);

    @Query("SELECT c.expiredDate, p.name, c.price, c.quantity, (c.quantity - c.couponRemain) " +
            "FROM CouponEntity c " +
            "JOIN c.product p " +
            "WHERE c.expiredDate < CURRENT_TIMESTAMP " +
            "AND p.business.id = :businessId")
    List<Object[]> findExpiredCouponsByBusinessId(@Param("businessId") int businessId);
}
