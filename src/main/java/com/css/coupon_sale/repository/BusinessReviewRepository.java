package com.css.coupon_sale.repository;


import com.css.coupon_sale.entity.BusinessReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessReviewRepository extends JpaRepository<BusinessReviewEntity, Integer> {


    @Query("SELECT COUNT(r) FROM BusinessReviewEntity r WHERE r.business.id = :business_id")
    int countByBusinessId(@Param("business_id") int business_id);

    @Query("SELECT AVG(r.count) FROM BusinessReviewEntity r WHERE r.business.id = :business_id")
    Double averageCountByBusinessId(@Param("business_id") int business_id);

    // Custom query method to check if a review exists for a given businessId and userId
    boolean existsByBusinessIdAndUserId(int business_id, Long user_id);

    List<BusinessReviewEntity> findAllByBusinessId(int business_id);
}