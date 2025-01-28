package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.NotificationRequest;
import com.css.coupon_sale.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotifications(Long receiverId);
    Long getUnreadNotificationCount(Long receiverId);
    void markNotificationAsRead(Long notificationId);
    void markAllAsRead(Long receiverId);
    NotificationResponse createNotification(NotificationRequest request);
    void deleteNotification(Long notificationId);
}
