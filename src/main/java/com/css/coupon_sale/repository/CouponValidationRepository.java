package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.CouponValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponValidationRepository extends JpaRepository<CouponValidationEntity, Integer> {

    @Query("SELECT c.customer.name as userName, c.customer.email as email, c.usedAt as usedAt, p.name as productName, " +
            "c.shop.name as businessName " +  // Fetch the business name
            "FROM CouponValidationEntity c " +
            "JOIN c.saleCoupon sc " +        // Join SaleCouponEntity
            "JOIN sc.coupon cp " +           // Join CouponEntity
            "JOIN cp.product p " +           // Join ProductEntity
            "JOIN c.shop s " +               // Join Shop entity to fetch business name
            "WHERE c.shop.id = :shopId")
    List<Object[]> findAllCouponUsages(@Param("shopId") Integer businessId);

}
