package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.request.ProductRequest1;
import com.css.coupon_sale.dto.request.ProductUpdateRequest;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.entity.ProductEntity;

import java.io.IOException;
import java.util.List;

public interface ProductService {


    ProductResponse saveProduct(ProductRequest request) throws IOException;

    List<ProductResponse> showByBusinessId(Integer id);

    List<ProductResponse> showAllProducts();

    ProductResponse showProductbyId(Integer id);

    ProductResponse updatebyId(Integer id, ProductUpdateRequest request) throws IOException;

    ProductResponse updateProductDiscount(Integer id, Float discount);

    void deletebyId(Integer id);

    ProductResponse findProductName(String name);

    ProductResponse findProductCategroy(String category);
}
