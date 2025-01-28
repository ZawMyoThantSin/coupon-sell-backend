package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.UserProfileRequest;
import com.css.coupon_sale.dto.response.UserListResponse;
import com.css.coupon_sale.dto.response.UserProfileResponse;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {
    UserProfileResponse updatebyId(Integer id, UserProfileRequest request) throws IOException;

    byte[] generateCustomerListReport(List<UserListResponse> customerResponses, String reportType) throws JRException;
}
