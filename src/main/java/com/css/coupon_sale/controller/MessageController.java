package com.css.coupon_sale.controller;

import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;
import com.css.coupon_sale.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final CustomWebSocketHandler webSocketHandler;

    @Autowired
    public MessageController(MessageService messageService, CustomWebSocketHandler webSocketHandler) {
        this.messageService = messageService;
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        try {
            MessageResponse response = messageService.sendMessage(request);

            // Notify the receiver in real-time via WebSocket
            webSocketHandler.sendToUser(request.getReceiverId(), response.getContent());

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/chat/{userId1}/{userId2}")
    public ResponseEntity<List<MessageResponse>> getChatMessages(
            @PathVariable Long userId1, @PathVariable Long userId2) {
        try {
            List<MessageResponse> responses = messageService.getChatMessages(userId1, userId2);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
