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
@Table(name = "qr_code")
public class QrCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sale_coupon_id", nullable = false)
    private SaleCouponEntity saleCoupon;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    private int status;

    @Column(name = "expired_date", nullable = false)
    private LocalDateTime expiredDate;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;
}

