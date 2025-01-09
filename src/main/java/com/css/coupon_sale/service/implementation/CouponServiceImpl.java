package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.BusinessCouponSalesResponse;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.ProductEntity;
import com.css.coupon_sale.repository.CouponRepository;
import com.css.coupon_sale.repository.ProductRepository;
import com.css.coupon_sale.service.CouponService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CouponResponse getCouponById(Integer id) {
        CouponEntity business = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponseDTO(business);
    }

    @Override
    public List<CouponResponse> getAllCouponlist() {
        return  couponRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse updateCoupon(Integer id, CouponRequest requestDTO) {
        CouponEntity coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        ProductEntity product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        coupon.setProduct(product);
        coupon.setCouponCode(requestDTO.getCouponCode());
        coupon.setDescription(requestDTO.getDescription());
        coupon.setPrice(requestDTO.getPrice());

        LocalDate localDate = LocalDate.parse(requestDTO.getExpiredDate());
        LocalTime localTime = LocalTime.now();
        LocalDateTime exp = localDate.atTime(localTime);

        coupon.setExpiredDate(exp);
        coupon.setQuantity(requestDTO.getQuantity());
        coupon.setCreatedAt(LocalDateTime.now());

        CouponEntity updatedBusiness = couponRepository.save(coupon);
        return mapToResponseDTO(updatedBusiness);
    }

    @Override
    public CouponResponse saveCoupon(CouponRequest requestDTO) {
        System.out.println("PID: "+requestDTO.getProductId());
        ProductEntity product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        System.out.println("REQ: "+ requestDTO.toString());
        CouponEntity coupon = new CouponEntity();
        coupon.setProduct(product);
        coupon.setCouponCode(requestDTO.getCouponCode());
        coupon.setDescription(requestDTO.getDescription());
        coupon.setPrice(requestDTO.getPrice());
        LocalDate localDate = LocalDate.parse(requestDTO.getExpiredDate());
        LocalTime localTime = LocalTime.now();
        LocalDateTime exp = localDate.atTime(localTime);

        coupon.setVisible(true);
        coupon.setExpiredDate(exp);
        coupon.setQuantity(requestDTO.getQuantity());
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setCouponRemain(requestDTO.getQuantity());
        CouponEntity savedBusiness = couponRepository.save(coupon);
        return mapToResponseDTO(savedBusiness);
    }

    @Override
    public List<CouponResponse> showByBusinessId(Integer id) {
        List<CouponEntity> productEntities = couponRepository.findByProduct_BusinessId(id);
        return productEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessCouponSalesResponse> getSoldCouponCountByBusiness(Integer businessId) {
        try {
            // Fetch results from the repository
            List<Object[]> results = couponRepository.getSoldCouponCountByBusiness(businessId);

            // Handle empty results gracefully
            if (results == null || results.isEmpty()) {
                System.out.println("No coupons found for business ID: " + businessId);
                return List.of();
            }

            // Map results to BusinessCouponSalesResponse objects
            List<BusinessCouponSalesResponse> responses = new ArrayList<>();
            for (Object[] result : results) {
                BusinessCouponSalesResponse response = new BusinessCouponSalesResponse(
                        ((Number) result[0]).intValue(), // saleCouponId
                        ((Number) result[1]).intValue(), // businessId
                        ((Number) result[2]).intValue(), // soldCount
                        (Date) result[3]                 // buyDate
                );
                responses.add(response);
            }
            return responses;
//            return results.stream()
//                    .map(result -> new BusinessCouponSalesResponse(
//                            ((Number) result[0]).intValue(), // saleCouponId
//                            ((Number) result[1]).intValue(), // businessId
//                            ((Number) result[2]).intValue(), // soldCount
//                            (Date) result[3]                 // buyDate
//                    ))
//                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error with full stack trace for better debugging
            System.err.println("Error fetching sold coupons for business ID " + businessId);
            e.printStackTrace();
        }

        // Return an empty list in case of error
        return List.of();
    }

    private CouponResponse mapToResponseDTO(CouponEntity coupon) {
        CouponResponse responseDTO = modelMapper.map(coupon, CouponResponse.class);
        responseDTO.setQuantity(coupon.getCouponRemain());
        responseDTO.setCouponCode(coupon.getProduct().getName());

        return responseDTO;
    }
}
