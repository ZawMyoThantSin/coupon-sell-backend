package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.BusinessCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessCategoryRepository extends JpaRepository<BusinessCategoryEntity, Integer> {

    boolean existsByName(String name);
}
