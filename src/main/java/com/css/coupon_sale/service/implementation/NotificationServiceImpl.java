package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.NotificationRequest;
import com.css.coupon_sale.dto.response.NotificationResponse;
import com.css.coupon_sale.entity.NotificationEntity;
import com.css.coupon_sale.repository.NotificationRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);


    @Autowired
    private UserRepository userRepository;

    @Override
    public List<NotificationResponse> getNotifications(Long receiverId) {
        List<NotificationEntity> notifications = notificationRepository.findAllByReceiver(receiverId);
        return notifications.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public Long getUnreadNotificationCount(Long receiverId) {
        return notificationRepository.countUnreadNotifications(receiverId);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new IllegalArgumentException("Notification not found"));
        notification.setIsRead(1);
        notificationRepository.save(notification);
    }

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Long receiverId = request.getReceiverId();

        // If no receiverId is provided, fetch the admin's user ID by role
        if (receiverId == null) {
            receiverId = userRepository.findAdminUserId()
                    .orElseThrow(() -> new IllegalArgumentException("Admin user not found."));
        }
        System.out.println("Receiver Id : " + receiverId );
        NotificationEntity notification = new NotificationEntity();
        notification.setReceiverId(receiverId);
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setRoute(request.getRoute());
        notification.setIsRead(0);

        NotificationEntity savedNotification = notificationRepository.save(notification);
        return convertToResponse(savedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long receiverId) {
        try {
            log.info("Marking all notifications as read for receiverId: {}", receiverId);
            notificationRepository.markAllAsRead(receiverId);
            log.info("Successfully marked all notifications as read for receiverId: {}", receiverId);
        } catch (Exception e) {
            log.error("Error marking notifications as read for receiverId: {}", receiverId, e);
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
    }

    @Override
    public void deleteNotification(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notificationRepository.delete(notification);
    }

    private NotificationResponse convertToResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setMessage(entity.getMessage());
        response.setType(entity.getType());
        response.setRoute(entity.getRoute());
        response.setIsRead(entity.getIsRead());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

}
