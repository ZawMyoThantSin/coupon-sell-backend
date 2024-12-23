package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.CustomerResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class AdminController {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<?> getAllCustomers(){
        List<UserEntity> userEntities = userRepository.findByRole("USER");
        List<CustomerResponse> responses = userEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private CustomerResponse mapToResponseDTO(UserEntity user) {
        return modelMapper.map(user, CustomerResponse.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> editUserFund(@PathVariable("id")Long id, @RequestBody String fund){
        UserEntity user  =  userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user ==null){
            return ResponseEntity.notFound().build();
        }
        user.setFunds(Integer.parseInt(fund));
        UserEntity updatedUser =  userRepository.save(user);
        CustomerResponse response = mapToResponseDTO(updatedUser);
        return ResponseEntity.ok(response);
    }
}
