package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.entity.UserEntity;

public interface AuthService {
    UserEntity register(SignupRequest request);

}
