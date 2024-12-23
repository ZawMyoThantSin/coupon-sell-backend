package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.ProductEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Integer> {

    @Query("select p from ProductEntity p where p.name=:n")
    public ProductEntity findProductName(@Param("n")String name);
    @Query("select p from ProductEntity p where p.category=:n")
    ProductEntity findProductCategory(String category);

    List<ProductEntity> findByBusiness_Id(Integer id);
}
