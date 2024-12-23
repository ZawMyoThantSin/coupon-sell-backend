package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with this email: " + email));

        if ("GOOGLE".equalsIgnoreCase(userEntity.getAuthProvider())) {
            // Return a user with no password validation
            return User.builder()
                    .username(userEntity.getEmail())
                    .password("") // No password needed
                    .roles(userEntity.getRole())
                    .build();
        }

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(getRoles(userEntity))
                .build();
    }

    private String getRoles(UserEntity user) {
        if (user.getRole() != null) {
            return user.getRole(); // Default role
        }
        return "USER"; // Return the role as a String
    }

}
