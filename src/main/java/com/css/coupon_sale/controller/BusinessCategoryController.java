package com.css.coupon_sale.controller;


import com.css.coupon_sale.dto.request.BusinessCategoryRequest;
import com.css.coupon_sale.dto.response.BusinessCategoryResponse;
import com.css.coupon_sale.service.BusinessCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/business-categories")
public class BusinessCategoryController {

    @Autowired
    private BusinessCategoryService categoryService;

    @PostMapping
    public ResponseEntity<BusinessCategoryResponse> createCategory(
            @RequestPart("name") String name) throws IOException {

        System.out.println("Received name: " + name);

        BusinessCategoryRequest requestDto = new BusinessCategoryRequest();
        requestDto.setName(name);
        BusinessCategoryResponse response = categoryService.createCategory(requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessCategoryResponse> updateCategory(@PathVariable int id, @RequestBody BusinessCategoryRequest requestDto) {
        BusinessCategoryResponse response = null;
        try {
            if (requestDto.getName() == null || requestDto.getName().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty.");
            }
            response = categoryService.updateCategory(id, requestDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessCategoryResponse> getCategoryById(@PathVariable int id) {
        BusinessCategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BusinessCategoryResponse>> getAllCategories() {
        List<BusinessCategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
