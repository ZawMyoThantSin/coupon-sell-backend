package com.css.coupon_sale.controller;

import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;
import com.css.coupon_sale.entity.MessageEntity;
import com.css.coupon_sale.repository.MessageRepository;
import com.css.coupon_sale.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final CustomWebSocketHandler webSocketHandler;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageService messageService, CustomWebSocketHandler webSocketHandler,
                             MessageRepository messageRepository) {
        this.messageService = messageService;
        this.webSocketHandler = webSocketHandler;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        try {
            MessageResponse response = messageService.sendMessage(request);

            long timestamp = response.getSendAt().toInstant(ZoneOffset.UTC).toEpochMilli();

            // Notify the receiver in real-time via WebSocket
            webSocketHandler.sendToUser1(request.getReceiverId(), Map.of(
                    "type", "newMessage",
                    "messageId", response.getId(),
                    "content", response.getContent(),
                    "senderId", request.getSenderId(),
                    "sendAt", timestamp
            ));

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit/{messageId}")
    public ResponseEntity<Void> editMessage(@PathVariable Long messageId, @RequestBody String newContent) {
        try {
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new EntityNotFoundException("Message not found"));
            System.out.println("Receiver Id - " + message.getReceiver().getId());
            messageService.editMessage(messageId, newContent);

            webSocketHandler.sendToUser1(message.getReceiver().getId(), Map.of(
                    "type", "edit",
                    "messageId", messageId,
                    "newContent", newContent
            ));
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        try {
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new EntityNotFoundException("Message not found"));
            System.out.println("Receiver Id - " + message.getReceiver().getId());
            System.out.println("Deleting message by id : "+messageId);
            messageService.deleteMessage(messageId);
            webSocketHandler.sendToUser1(message.getReceiver().getId(), Map.of(
                    "type", "delete",
                    "messageId", messageId
            ));
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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

    @PostMapping("/react/{messageId}/{userId}")
    public ResponseEntity<Void> reactToMessage(@PathVariable Long messageId,
                                               @PathVariable Long userId,
                                               @RequestBody String reaction) {
        try {
            messageService.addReaction(messageId, userId, reaction);

            // Notify the receiver in real-time via WebSocket
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new EntityNotFoundException("Message not found"));
            webSocketHandler.sendToUser1(message.getSender().getId(), Map.of(
                    "type", "reactionAdded",
                    "messageId", messageId,
                    "userId", userId,
                    "reaction", reaction
            ));

            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/unreact/{messageId}/{userId}")
    public ResponseEntity<Void> removeReaction(@PathVariable Long messageId,
                                               @PathVariable Long userId) {
        try {
            messageService.removeReaction(messageId, userId);
            // Notify the receiver in real-time via WebSocket
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new EntityNotFoundException("Message not found"));
            webSocketHandler.sendToUser1(message.getSender().getId(), Map.of(
                    "type", "reactionRemoved",
                    "messageId", messageId,
                    "userId", userId
            ));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
