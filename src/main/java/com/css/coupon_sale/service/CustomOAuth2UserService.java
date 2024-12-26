package com.css.coupon_sale.service;

import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
//  Extract information from oauth login
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profile = oAuth2User.getAttribute("picture");
        String sub = oAuth2User.getAttribute("sub");
        System.out.println("Profile Pic: "+ profile);
        // Save or update the user in the database
        userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity user = new UserEntity();
            System.out.println("hdfsjkadhgjhkahgkjsdghkhsfkahgakd");
            user.setEmail(email);
            user.setName(name);
            user.setSub(sub);
            user.setRole("USER");
            user.setProfile(profile);
            user.setAuthProvider("GOOGLE");
            user.setCreated_at(LocalDateTime.now());
            return userRepository.save(user);
        });

        return oAuth2User;
    }
}