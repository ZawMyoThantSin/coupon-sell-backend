package com.css.coupon_sale.filter;

import com.css.coupon_sale.service.implementation.CustomUserDetailService;
import com.css.coupon_sale.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService userDetailService;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailService userDetailService) {
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            System.out.println("Request URI: " + request.getRequestURI());
            String authHeader = request.getHeader("Authorization");
            System.out.println("Auth header: " + authHeader);
            String token = null;
            String email = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtUtil.extractUsername(token);
                System.out.println("username: " + email);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                System.out.println("User Detail return: " + userDetails);

                if (jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("Token validated successfully");
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("Authentication set for: " + userDetails.getUsername());
                } else {
                    throw new RuntimeException("Invalid or expired token");
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(498);
            response.getWriter().write("Token has expired");
            System.out.println("In EXP");
        } catch (SignatureException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("In Sing");
            response.getWriter().write("Invalid token signature");
        } catch (MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Malformed token");
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("in here");
            response.getWriter().write("Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred: " + e.getMessage());
        }
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("Request URI: " + request.getRequestURI());
//        String authHeader = request.getHeader("Authorization");
//        System.out.println("Auth header: " + authHeader);
//        String token = null;
//        String email = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")){
//            token = authHeader.substring(7);
//            email = jwtUtil.extractUsername(token);
//            System.out.println("username"+ email);
//        }
//
//        if(email != null && SecurityContextHolder.getContext().getAuthentication() ==null) {
//            UserDetails userDetails = userDetailService.loadUserByUsername(email);
//            System.out.println("User Detail return: " + userDetails);
//
//            if (jwtUtil.validateToken(token, userDetails)) {
//                System.out.println("Token validated successfully");
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                System.out.println("Authentication set for: " + userDetails.getUsername());
//            }
//        }
//        filterChain.doFilter(request, response);
}

