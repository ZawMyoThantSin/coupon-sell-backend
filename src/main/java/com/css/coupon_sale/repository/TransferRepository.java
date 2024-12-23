package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.entity.TransferEntity;
import com.css.coupon_sale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferEntity, Integer> {
    List<TransferEntity> findBySenderOrAccepter(UserEntity sender, UserEntity accepter);

    boolean existsBySenderAndAccepterAndSaleCouponAndStatus(UserEntity sender, UserEntity accepter, SaleCouponEntity saleCoupon, int status);

}
