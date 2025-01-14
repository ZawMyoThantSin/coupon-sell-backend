package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.BusinessReviewRequest;
import com.css.coupon_sale.dto.response.BusinessReviewResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.BusinessReviewEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.BusinessReviewRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.BusinessReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessReviewServiceImpl implements BusinessReviewService {
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessReviewRepository businessReviewRepository;
    private final ModelMapper modelMapper;

    public BusinessReviewServiceImpl(BusinessRepository businessRepository, UserRepository userRepository, BusinessReviewRepository businessReviewRepository, ModelMapper modelMapper ) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.businessReviewRepository = businessReviewRepository;
        this.modelMapper = modelMapper;
    }

    public boolean hasUserRated(int business_id, Long user_id) {
        return businessReviewRepository.existsByBusinessIdAndUserId(business_id, user_id);
    }

    @Override
    public BusinessReviewResponse getByBusinessId(int id) {
        BusinessReviewEntity business = businessReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponseDTO(business);
    }

    @Override
    public List<BusinessReviewResponse> getAllRatingsByBusinessId(int business_id) {
        List<BusinessReviewEntity> reviews = businessReviewRepository.findAllByBusinessId(business_id);
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BusinessReviewResponse rateBusiness(BusinessReviewRequest dto) throws IOException {
        UserEntity user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessEntity business = businessRepository.findById(dto.getBusiness_id())
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));


        BusinessReviewEntity rate = new BusinessReviewEntity();


        rate.setCount(dto.getCount());
        rate.setMessage(dto.getMessage());
        rate.setUser(user);
        rate.setBusiness(business);

        rate.setCreatedAt(LocalDateTime.now());
        System.out.println("Before rate: "+ business);
        BusinessReviewEntity ratebusiness = businessReviewRepository.save(rate);
        return mapToResponseDTO(ratebusiness);
    }



    @Override
    public double calculateOverviewCount(int business_id) {
        int totalUsers = businessReviewRepository.countByBusinessId(business_id);
        if (totalUsers == 0) {
            return 0.0;
        }
        Double average = businessReviewRepository.averageCountByBusinessId(business_id);
        return average != null ? average : 0.0;
    }


    private BusinessReviewResponse mapToResponseDTO(BusinessReviewEntity business) {
        BusinessReviewResponse responseDTO = modelMapper.map(business, BusinessReviewResponse.class);

        responseDTO.setUserName(business.getUser().getName());
        responseDTO.setProfile(business.getUser().getProfile());
        responseDTO.setUser_id(business.getUser().getId());
        responseDTO.setBusiness_id(business.getBusiness().getId());
        return responseDTO;
    }
}
