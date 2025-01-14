package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    OtpEntity findByEmail(String email);
}
