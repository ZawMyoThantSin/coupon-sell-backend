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
@Table(name = "transfer")
public class TransferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "accepter_id", nullable = false)
    private UserEntity accepter;

    @ManyToOne
    @JoinColumn(name = "sale_coupon_id", nullable = false)
    private SaleCouponEntity saleCoupon;

    private int status;

    @Column(name = "transfer_at", nullable = false)
    private LocalDateTime transferAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getAccepter() {
        return accepter;
    }

    public void setAccepter(UserEntity accepter) {
        this.accepter = accepter;
    }

    public SaleCouponEntity getSaleCoupon() {
        return saleCoupon;
    }

    public void setSaleCoupon(SaleCouponEntity saleCoupon) {
        this.saleCoupon = saleCoupon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTransferAt() {
        return transferAt;
    }

    public void setTransferAt(LocalDateTime transferAt) {
        this.transferAt = transferAt;
    }
}

