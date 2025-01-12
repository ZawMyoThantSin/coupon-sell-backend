package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.entity.UserEntity;

import java.util.List;

public interface AuthService {
    UserEntity register(SignupRequest request);

    UserResponse searchUserByEmail(String email);
}
