package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.QrCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCodeEntity, Integer> {

}
