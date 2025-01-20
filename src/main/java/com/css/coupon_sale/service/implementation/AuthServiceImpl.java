package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity register(SignupRequest request) {
        Optional<UserEntity> oUser = userRepository.findByEmail(request.getEmail());
        if (oUser.isPresent()){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setPassword(passwordEncoder.encode(CharBuffer.wrap(request.getPassword())));
        if(userEntity.getRole() == null){
            userEntity.setRole("USER");
        }
        userEntity.setAuthProvider("LOCAL");
        userEntity.setCreated_at(now());

        return  userRepository.save(userEntity);
    }

    @Override
    public UserResponse searchUserByEmail(String email) {
        // Find matching users by email
        UserEntity users = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User Not Found with this email: "+ email));
        if (users!= null){
            UserResponse response = new UserResponse();
            response.setEmail(users.getEmail());
            return response;
        }
        return new UserResponse();
        // Map the user entities to user responses
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // Fetch user by userId
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Check if the old password matches the user's current password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        // Encode and set the new password
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        userRepository.save(user);
    }
}
