package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity,Integer> {
    List<CouponEntity> findByProduct_Business_Id(int businessId);
    List<CouponEntity> findByProduct_BusinessId(int businessId);
}
