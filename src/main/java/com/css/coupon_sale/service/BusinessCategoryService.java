package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.BusinessCategoryRequest;
import com.css.coupon_sale.dto.response.BusinessCategoryResponse;

import java.io.IOException;
import java.util.List;

public interface BusinessCategoryService {

    BusinessCategoryResponse createCategory(BusinessCategoryRequest requestDto) throws IOException;
    BusinessCategoryResponse updateCategory(int id, BusinessCategoryRequest requestDto) throws IOException;
    BusinessCategoryResponse getCategoryById(int id);
    List<BusinessCategoryResponse> getAllCategories();
    void deleteCategory(int id);
}
