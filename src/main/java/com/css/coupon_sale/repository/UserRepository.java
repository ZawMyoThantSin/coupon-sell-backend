package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(String role);
}
