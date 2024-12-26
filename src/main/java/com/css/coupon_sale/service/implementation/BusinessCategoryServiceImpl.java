package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.BusinessCategoryRequest;
import com.css.coupon_sale.dto.response.BusinessCategoryResponse;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.entity.BusinessCategoryEntity;
import com.css.coupon_sale.repository.BusinessCategoryRepository;
import com.css.coupon_sale.service.BusinessCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessCategoryServiceImpl implements BusinessCategoryService {

    @Autowired
    private BusinessCategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;

    @Override
    public BusinessCategoryResponse createCategory(BusinessCategoryRequest requestDto) throws IOException {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        BusinessCategoryEntity category = new BusinessCategoryEntity();
        category.setName(requestDto.getName());
        category.setCreatedAt(LocalDateTime.now());

        category = categoryRepository.save(category);

        return mapToResponseDto(category);
    }

    @Override
    public BusinessCategoryResponse updateCategory(int id, BusinessCategoryRequest requestDto) throws IOException {
        BusinessCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(requestDto.getName());
        category.setUpdatedAt(LocalDateTime.now());

        category = categoryRepository.save(category);

        return mapToResponseDto(category);
    }

    @Override
    public BusinessCategoryResponse getCategoryById(int id) {
        BusinessCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return mapToResponseDto(category);
    }

    @Override
    public List<BusinessCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    private BusinessCategoryResponse mapToResponseDto(BusinessCategoryEntity category) {
        BusinessCategoryResponse responseDto = mapper.map(category, BusinessCategoryResponse.class);
        return responseDto;
    }
}
