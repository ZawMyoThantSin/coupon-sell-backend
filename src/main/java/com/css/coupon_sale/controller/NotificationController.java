package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.NotificationRequest;
import com.css.coupon_sale.dto.response.NotificationResponse;
import com.css.coupon_sale.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestParam("receiverId") Long receiverId) {
        List<NotificationResponse> notifications = notificationService.getNotifications(receiverId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@RequestParam("receiverId") Long receiverId) {
        Long count = notificationService.getUnreadNotificationCount(receiverId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@RequestParam("receiverId") Long receiverId) {
        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }
        notificationService.markAllAsRead(receiverId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
