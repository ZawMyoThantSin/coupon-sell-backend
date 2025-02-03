package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.UserProfileRequest;
import com.css.coupon_sale.dto.response.UserProfileResponse;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.UserProfileService;
import com.css.coupon_sale.service.implementation.CustomUserDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final UserProfileService userProfileService;

    @Autowired
    public UserController(UserRepository userRepository, ModelMapper modelMapper, UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userProfileService = userProfileService;
    }
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
//        if (userDetails == null) {
//            System.out.println("In USRCON: UserDetails is null");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//        }
//        String email = userDetails.getUsername();
//        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User Not Found with this Email"+ email));
//        if(user == null){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
//        }
//        UserProfileResponse response = new UserProfileResponse();
//        response.setId(user.getId());
//        response.setName(user.getName());
//        response.setEmail(user.getEmail());
//        response.setRole(user.getRole());
//        response.setProfile(user.getProfile());
//        response.setEnableNoti(user.getEnable_noti());
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String email;

        // Handle OAuth2 users
        if (principal instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) principal;
            email = oauthUser.getAttribute("email");
        }
        // Handle normal users
        else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            email = userDetails.getUsername();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unsupported authentication type");
        }

        // Fetch user from the database
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with this Email: " + email));

        // Prepare response
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setProfile(user.getProfile());
        response.setEnableNoti(user.getEnable_noti());
        response.setAuthProvider(user.getAuthProvider());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCreate_at(user.getCreated_at());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updatebyId(@PathVariable("id")Integer id, @ModelAttribute UserProfileRequest request) throws IOException, IOException {
        UserProfileResponse response =userProfileService.updatebyId(id, request);
        if (request.getPhone().isEmpty()){
            System.out.println("Image not found");
        }
        if (response == null ){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok (response);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalUsersCount(){
        long userCount = userRepository.countByRoleNotAdmin();
        long todayUserCount = userRepository.countUsersCreatedToday();

        Map<String, Long> response = new HashMap<>();
        response.put("totalUsersCount", userCount);
        response.put("todayUserCount", todayUserCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBusinessById(@PathVariable Long id) {
        UserEntity business = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(business);
    }
}
