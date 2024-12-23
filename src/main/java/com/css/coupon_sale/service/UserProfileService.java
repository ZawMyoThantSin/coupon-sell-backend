package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.UserProfileRequest;
import com.css.coupon_sale.dto.response.UserProfileResponse;

import java.io.IOException;

public interface UserProfileService {
    UserProfileResponse updatebyId(Integer id, UserProfileRequest request) throws IOException;
}
