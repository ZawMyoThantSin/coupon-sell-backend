package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.CouponValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponValidationRepository extends JpaRepository<CouponValidationEntity, Integer>  {
    @Query("SELECT c.customer.name, c.customer.email, c.usedAt, p.name " +
            "FROM CouponValidationEntity c " +
            "JOIN c.saleCoupon sc " +      // Join SaleCouponEntity
            "JOIN sc.coupon cp " +         // Join CouponEntity
            "JOIN cp.product p " +         // Join ProductEntity
            "WHERE c.shop.id = :shopId")
    List<Object[]> findAllCouponUsages(@Param("shopId") Integer shopId);
}
