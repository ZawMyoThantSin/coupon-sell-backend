package com.css.coupon_sale.repository;

import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.entity.TransferEntity;
import com.css.coupon_sale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferEntity, Integer> {
    List<TransferEntity> findBySenderOrAccepter(UserEntity sender, UserEntity accepter);

    boolean existsBySenderAndAccepterAndSaleCouponAndStatus(UserEntity sender, UserEntity accepter, SaleCouponEntity saleCoupon, int status);
//
//    @Query("SELECT t FROM TransferEntity t WHERE t.accepter.id = :accepterId")
    List<TransferEntity> findByAccepter_Id(Long accepterId);

//    @Query("SELECT new com.example.dto.CouponResponse(sc.id, sc.productName, sc.discount, sc.expiryDate, sc.imageUrl) " +
//            "FROM Transfer t " +
//            "JOIN SaleCoupon sc ON t.saleCouponId = sc.id " +
//            "WHERE t.accepterId = :accepterId")
//    List<CouponResponse> findCouponsByAccepterId(@Param("accepterId") Long accepterId);

}
