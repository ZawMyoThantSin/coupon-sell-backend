package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.request.ProductRequest1;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.ProductEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.CouponRepository;
import com.css.coupon_sale.repository.ProductRepository;
import com.css.coupon_sale.service.BusinessService;
import com.css.coupon_sale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ProductService productService;
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@ModelAttribute ProductRequest productRequest) throws IOException {
        if(!productRequest.getImageFile().isEmpty()){
            System.out.println("Not emp");
        }
        System.out.println("InController");
        productService.saveProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully!");
    }

    @GetMapping("/ppp/{id}")
    public ResponseEntity<Map<String,?>> TestUser(@PathVariable("id") Integer id){

        System.out.println(id);
        List<ProductResponse> productEntities = productService.showByBusinessId(id);
        for (ProductResponse p :productEntities){
            System.out.println(p.getName());
        }
        System.out.println(productEntities.isEmpty());
;
        List<CouponEntity> couponEntities =  couponRepository.findByProduct_Business_Id(id);
        System.out.println(couponEntities.isEmpty());
        Map<String,List<?>> res = new HashMap<>();
        res.put("products",productEntities);
        res.put("coupons", couponEntities);
        return ResponseEntity.ok(res);
    }
}
