package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity,Integer> {
    @Query("SELECT b FROM BusinessEntity b WHERE b.status = true")
    List<BusinessEntity> findAllActiveBusinesses();
//  find by user_id
    BusinessEntity findByUser_Id(Long userId);
}
