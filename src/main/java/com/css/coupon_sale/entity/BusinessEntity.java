package com.css.coupon_sale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "business")
public class BusinessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false )
    private String location;

    @Column(columnDefinition = "TEXT  ")
    private String description;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    private String photo;

    @ManyToOne
    @JoinColumn(name = "business_category_id", nullable = false)
    private BusinessCategoryEntity category;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "total_income")
    private Double totalIncome;

    @Column(name = "last_paid_amount", nullable = false)
    private Double lastPaidAmount;

    @Column(name = "payment_status", columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    private String paymentStatus; // e.g., PENDING, PAID

    @Column(name = "income_increased", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean incomeIncreased;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BusinessCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(BusinessCategoryEntity category) {
        this.category = category;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getLastPaidAmount() {
        return lastPaidAmount;
    }

    public void setLastPaidAmount(Double lastPaidAmount) {
        this.lastPaidAmount = lastPaidAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isIncomeIncreased() {
        return incomeIncreased;
    }

    public void setIncomeIncreased(boolean incomeIncreased) {
        this.incomeIncreased = incomeIncreased;
    }

    @Override
    public String toString() {
        return "BusinessEntity{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", photo='" + photo + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
