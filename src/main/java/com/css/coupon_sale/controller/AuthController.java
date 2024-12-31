package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.LoginRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.HttpResponse;
import com.css.coupon_sale.dto.response.LoginResponse;
import com.css.coupon_sale.dto.response.SignupResponse;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.AuthService;
import com.css.coupon_sale.service.implementation.CustomUserDetailService;
import com.css.coupon_sale.util.JwtUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, CustomUserDetailService userDetailService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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
            return ResponseEntity.status(403).body(new LoginResponse("Invalid credentials"));
        }
        UserEntity oUser= userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new AppException("User Not Found",HttpStatus.NOT_FOUND));

        UserDetails userDetails;
        try {
            // Load user details
            userDetails = userDetailService.loadUserByUsername(loginRequest.getEmail());
        } catch (UsernameNotFoundException e) {
            // User not found
            System.out.println("IN Here:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse("User not found"));
        }
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER"); // Default role if no roles are found
        System.out.println("In Login role:"+ role);

        // Generate JWT
        String jwt = jwtUtil.generateToken(userDetails.getUsername(),role,oUser.getId());//error

        // Return response with JWT
        return ResponseEntity.ok(new LoginResponse(jwt));
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
}
