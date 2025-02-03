package com.css.coupon_sale.config;

import com.css.coupon_sale.dto.ChatMessage;
import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.service.MessageService;
import com.css.coupon_sale.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.websocket.OnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

        // Map to track connected users by user ID
        private final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
        private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON serialization

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private MessageService messageService;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            String token = getTokenFromSession(session); // Extract token from session
            if (token != null && validateToken(token)) {
                Long userId = extractUserIdFromToken(token);
                String role = extractUserRoleFromToken(token);
                if (userId != null) {
                    session.getAttributes().put("userId", userId);
                    session.getAttributes().put("role", role);
                    userSessions.put(userId, session);
                    String message = "This is websocket testing";
                    sendToUser(userId,  message);
                    System.out.println("WebSocket connection established for user ID: " + userId + "Role: "+ role);
                } else {
                    closeSessionWithError(session, "Failed to extract user ID from token.");
                }
            } else {
                closeSessionWithError(session, "Invalid or missing JWT token.");
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            Long userId = getUserIdFromSession(session);
            if (userId != null) {
                userSessions.remove(userId);
                System.out.println("WebSocket connection closed for user: " + userId);
            }
        }

        @OnMessage
        protected void handleTextMessage(WebSocketSession session, String message) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(message);

                if ("PING".equals(jsonNode.get("type").asText())) {
                    // Handle PING messages
                    return;
                }

                // Handle other messages that require a user ID
                String userId = jsonNode.get("userId").asText();
                if (userId == null) {
                    throw new IllegalArgumentException("User ID must not be null");
                }

                // Proceed with the business logic using the userId
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error processing WebSocket message", e);
            }
        }

        public void broadcast(String message) {
            userSessions.forEach((userId, session) -> {
                if (session.isOpen()) {
                    sendMessage(session, message);
                }
            });
        }

        public void sendToRole(String role, String message) {
            for (WebSocketSession session : userSessions.values()) {
                if (session.isOpen() && role.equals(session.getAttributes().get("role"))) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        public void sendToUser(Long userId, String message) {
            WebSocketSession session = userSessions.get(userId);
            if (session != null && session.isOpen()) {
                sendMessage(session, message);
            } else {
                System.out.println("User with ID " + userId + " is not connected.");
            }
        }

        private void sendMessage(WebSocketSession session, String message) {
            try {

                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        }

        public void sendToUser1(Long userId, Map<String, Object> messageData) {
            WebSocketSession session = userSessions.get(userId);
            if (session != null && session.isOpen()) {
                sendMessage1(session, messageData);
            } else {
                System.out.println("User with ID " + userId + " is not connected.");
            }
        }

        private void sendMessage1(WebSocketSession session, Map<String, Object> messageData) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(messageData);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        }

        private String getTokenFromSession(WebSocketSession session) {
            String query = session.getUri().getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && "token".equals(pair[0])) {
                        return pair[1];
                    }
                }
            }
            return null;
        }

        private Long getUserIdFromSession(WebSocketSession session) {
            String token = getTokenFromSession(session);
            return token != null && validateToken(token) ? extractUserIdFromToken(token) : null;
        }


        private void closeSessionWithError(WebSocketSession session, String errorMessage) {
            System.out.println("Closing session: " + errorMessage);
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
                System.err.println("Error closing session: " + e.getMessage());
            }
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
        private String  extractUserRoleFromToken(String token) {
            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                return claims.get("role", String.class);
            } catch (Exception e) {
                System.err.println("Failed to extract user ID from token: " + e.getMessage());
                return null;
        }
    }


}
