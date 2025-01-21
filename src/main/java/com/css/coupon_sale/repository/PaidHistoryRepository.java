package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.PaidHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaidHistoryRepository extends JpaRepository<PaidHistoryEntity, Long> {

    @Query("SELECT p FROM PaidHistoryEntity p WHERE p.business.id = :businessId ORDER BY p.paymentDate DESC")
    List<PaidHistoryEntity> findByBusinessIdOrderByPaymentDateDesc(@Param("businessId") int businessId);

}
