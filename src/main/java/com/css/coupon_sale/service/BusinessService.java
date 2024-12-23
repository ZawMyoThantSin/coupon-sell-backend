package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.BusinessRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.SignupResponse;

import java.io.IOException;
import java.util.List;

public interface BusinessService {
    SignupResponse addBusinessOwner(SignupRequest request);
    BusinessResponse createBusiness(BusinessRequest requestDTO) throws IOException;
    BusinessResponse getBusinessById(Integer id);
    List<BusinessResponse> getByUserId(Long id);
    List<BusinessResponse> getAllBusinesses();
    BusinessResponse updateBusiness(Integer id, BusinessRequest requestDTO);
    BusinessResponse softDeleteBusiness(Integer id);
}
