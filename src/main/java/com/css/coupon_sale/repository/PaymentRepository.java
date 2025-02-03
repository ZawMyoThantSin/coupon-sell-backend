package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {
    @Query("SELECT COUNT(p) FROM PaymentEntity p")
    long countAllBusinesses();
}
