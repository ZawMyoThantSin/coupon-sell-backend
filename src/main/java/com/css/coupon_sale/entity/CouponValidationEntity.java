package com.css.coupon_sale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_validation")
public class CouponValidationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "sale_coupon_id", nullable = false)
    private SaleCouponEntity saleCoupon;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private BusinessEntity shop;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "validator_id", nullable = false)
    private UserEntity validator;
}

