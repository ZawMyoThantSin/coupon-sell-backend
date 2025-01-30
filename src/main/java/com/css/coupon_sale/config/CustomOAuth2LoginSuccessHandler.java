package com.css.coupon_sale.config;

import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String token = null;
        if("OWNER".equals(user.getRole())){
             token = jwtUtil.generateToken(email, user.getRole(),user.getId());
        }else {
             token = jwtUtil.generateToken(email, "ROLE_"+user.getRole(),user.getId());
        }


        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + token + "\"}");
        response.sendRedirect(allowedOrigin+"/login?token="+ token);
    }
}
