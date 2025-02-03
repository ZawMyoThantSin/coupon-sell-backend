package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.response.BusinessCategoryResponse;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.BusinessCategoryService;
import com.css.coupon_sale.service.BusinessService;
import com.css.coupon_sale.service.CouponService;
import com.css.coupon_sale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/public/")
public class PublicController {
    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;

    private final BusinessService businessService;
    private final BusinessCategoryService categoryService;
    private final ProductService productService;
    private final CouponService couponService;

    @Autowired
    public PublicController(BusinessService businessService, BusinessCategoryService categoryService, ProductService productService, CouponService couponService) {
        this.businessService = businessService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.couponService = couponService;
    }


    // Controller Method
    @GetMapping("/products/images/{filename}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename) throws MalformedURLException, IOException {
        Path imagePath = Paths.get(uploadDir + "/product").resolve(filename);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() && imageResource.isReadable()) {
            // Dynamically determine the content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback content type
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Controller Method
    @GetMapping("/profile/images/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) throws MalformedURLException, IOException {
        Path imagePath = Paths.get(uploadDir + "/profile").resolve(filename);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() && imageResource.isReadable()) {
            // Dynamically determine the content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback content type
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/businesses/images/{filename}")
    public ResponseEntity<Resource> getBusinessImage(@PathVariable String filename) throws MalformedURLException, IOException {
        Path imagePath = Paths.get(uploadDir + "/business").resolve(filename);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() && imageResource.isReadable()) {
            // Dynamically determine the content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback content type
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/payments/qr/{filename}")
    public ResponseEntity<Resource> getAdminQrImage(@PathVariable String filename) throws MalformedURLException, IOException {
        Path imagePath = Paths.get(uploadDir + "/qr_images").resolve(filename);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() && imageResource.isReadable()) {
            // Dynamically determine the content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback content type
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/orders/{filename}")
    public ResponseEntity<Resource> getOrderScreenShot(@PathVariable String filename) throws MalformedURLException, IOException {
        Path imagePath = Paths.get(uploadDir + "/order").resolve(filename);
        Resource imageResource = new UrlResource(imagePath.toUri());

        if (imageResource.exists() && imageResource.isReadable()) {
            // Dynamically determine the content type
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback content type
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessResponse>> getAllBusinesses() {
        List<BusinessResponse> businesses = businessService.getAllBusinesses();
        return ResponseEntity.ok(businesses);
    }
    @GetMapping("/business-categories")
    public ResponseEntity<List<BusinessCategoryResponse>> getAllCategories() {
        List<BusinessCategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> showAllCourses(){
        List<ProductResponse> response =productService.showAllProducts();
        return  ResponseEntity.ok(response);
    }
    @GetMapping("/products/{id}")
    public  ResponseEntity<ProductResponse> showById(@PathVariable("id")Integer id){
        ProductResponse response =productService.showProductbyId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponse>> getAllCoupon() {
        List<CouponResponse> businesses = couponService.getAllCouponlist();
        return ResponseEntity.ok(businesses);
    }
    @GetMapping("/coupons/view/{id}")
    public ResponseEntity<Boolean> increaseViewCount(@PathVariable Integer id) {
        couponService.increaseViewCount(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/businesses/{id}")
    public ResponseEntity<BusinessResponse> getBusinessById(@PathVariable Integer id) {
        BusinessResponse business = businessService.getBusinessById(id);
        return ResponseEntity.ok(business);
    }

    @GetMapping("/products/b/{id}")
    public ResponseEntity<List<ProductResponse>> getByBusiness(@PathVariable("id")Integer id){
        List<ProductResponse> responses = productService.showByBusinessId(id);
        if(responses != null){
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.notFound().build();
    }
}