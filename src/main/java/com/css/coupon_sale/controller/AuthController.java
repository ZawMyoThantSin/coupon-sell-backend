package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.LoginRequest;
import com.css.coupon_sale.dto.request.ResetPasswordRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.HttpResponse;
import com.css.coupon_sale.dto.response.LoginResponse;
import com.css.coupon_sale.dto.response.SignupResponse;
import com.css.coupon_sale.dto.response.UserResponse;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.AuthService;
import com.css.coupon_sale.service.EmailService;
import com.css.coupon_sale.service.implementation.CustomUserDetailService;
import com.css.coupon_sale.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, CustomUserDetailService userDetailService, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        try {
            // Authenticate the user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

        } catch (AuthenticationException ex) {
            // Invalid credentials
            System.out.println("IN Here:" + ex.getMessage());
            return ResponseEntity.status(403).body(new LoginResponse("null","Invalid credentials"));
        }
        UserEntity oUser= userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new AppException("User Not Found",HttpStatus.NOT_FOUND));

        UserDetails userDetails;
        try {
            // Load user details
            userDetails = userDetailService.loadUserByUsername(loginRequest.getEmail());
        } catch (UsernameNotFoundException e) {
            // User not found
            System.out.println("IN Here:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse("null","User not found"));
        }
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER"); // Default role if no roles are found
        System.out.println("In Login role:"+ role);

        if ("OWNER".equals(oUser.getRole())) {
            if (oUser.getStatus() == 1) {
                // Owner needs to reset their password
                String token = jwtUtil.generateToken(oUser.getEmail(), oUser.getRole(), oUser.getId());
                return ResponseEntity.ok(new LoginResponse(token, "RESET_PASSWORD_REQUIRED"));
            } else if (oUser.getStatus() == 0) {
                // Owner can proceed to their dashboard
                String token = jwtUtil.generateToken(oUser.getEmail(), oUser.getRole(), oUser.getId());
                return ResponseEntity.ok(new LoginResponse(token, "LOGIN_SUCCESSFUL"));
            }
        }

        // Generate JWT
        String jwt = jwtUtil.generateToken(userDetails.getUsername(),role,oUser.getId());//error

        // Return response with JWT
        return ResponseEntity.ok(new LoginResponse(jwt,"Success"));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest request){
        UserEntity createdUserEntity = authService.register(request);
        if (createdUserEntity != null){
            SignupResponse response= new SignupResponse(
                    createdUserEntity.getCreated_at(),
                    createdUserEntity.getId(),
                    createdUserEntity.getName(),
                    createdUserEntity.getEmail()
            );
            HttpResponse<SignupResponse> res = new HttpResponse<>();
            res.setStatus(true);
            res.setMessage("User Created Successfully");
            res.setData(response);

            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }else return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail to create User");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request, @RequestHeader("Authorization") String authHeader) {
        // Validate and parse token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or malformed");
        }

        String token = authHeader.replace("Bearer ", "");
        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(token);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // Validate role and ID from token
        Long tokenUserId = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        System.out.println("Role " + role);
        System.out.println("User Id " + tokenUserId);


        if (!"OWNER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only owners can reset passwords");
        }

//        if (!tokenUserId.equals(request.getUserId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User ID mismatch");
//        }

        // Retrieve user and update password
        UserEntity user = userRepository.findById(tokenUserId)
                .orElseThrow(() -> {
                    System.out.println("User not found for ID: " + request.getUserId());
                    return new AppException("User Not Found", HttpStatus.NOT_FOUND);
                });

        System.out.println("Username is  " + user.getName());

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);
        user.setStatus(0); // Mark as active
        user.setUpdated_at(LocalDateTime.now());
        try {
            userRepository.save(user);
            System.out.println("User password updated successfully for ID: " + user.getId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successful"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user in the database");
        }
    }

    @PostMapping("/public/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordReset request){
        UserEntity user = userRepository.findByEmail(request.email)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with this email: "+ request.email));
        try{
            if (user != null){
                user.setPassword(passwordEncoder.encode(request.password));
                user.setUpdated_at(LocalDateTime.now());
                userRepository.save(user);
                return ResponseEntity.ok().build();
            }
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleInvalidSignature(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature");
    }

    @GetMapping("/search-user")
    public ResponseEntity<UserResponse> searchUsersByEmail(
            @RequestParam("email") String email) {
        UserResponse responses = authService.searchUserByEmail(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public/send-otp/{email}")
    public ResponseEntity<String> sendOTPMail(@PathVariable("email") String email){
        String otp = emailService.generateOtp();
        emailService.sendOtp(email, otp);
        emailService.saveOtp(email, otp);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/public/validate")
    public ResponseEntity<Boolean> validateOtp(@RequestBody OtpRequest otpRequest) {
        String status = emailService.validateOtp(otpRequest.email, otpRequest.otp);
        if ("SUCCESS".equals(status)) {
            return ResponseEntity.ok(true);
        } else if("EXP".equals(status)){
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.badRequest().body(false);
    }

    public record OtpRequest(String email,String otp){};
    public record PasswordReset(String email, String password){};
}
