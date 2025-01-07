package com.css.coupon_sale.config;

import com.css.coupon_sale.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            // Extract JWT token from query parameters
            String query = servletRequest.getServletRequest().getQueryString();
            if (query != null) {
                String token = getTokenFromQuery(query);
                if (token != null && validateToken(token)) {
                    // Extract user ID and add it to the WebSocket session attributes
                    Long userId = extractUserIdFromToken(token);
                    if (userId != null) {
                        attributes.put("userId", userId);
                        System.out.println("Handshake successful for user ID: " + userId);
                        return true;
                    }
                }
            }
        }

        System.out.println("Handshake failed: Invalid or missing JWT token.");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // Reject handshake
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // No additional actions needed after handshake
    }

    private String getTokenFromQuery(String query) {
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "token".equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            return !jwtUtil.isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    private Long extractUserIdFromToken(String token) {
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            return claims.get("id", Long.class);
        } catch (Exception e) {
            System.err.println("Failed to extract user ID from token: " + e.getMessage());
            return null;
        }
    }
}
