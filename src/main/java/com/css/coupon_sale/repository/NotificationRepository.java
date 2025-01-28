package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n WHERE (n.receiverId = :receiverId OR n.receiverId IS NULL) ORDER BY n.createdAt DESC")
    List<NotificationEntity> findAllByReceiver(Long receiverId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE (n.receiverId = :receiverId OR n.receiverId IS NULL) AND n.isRead = 0")
    Long countUnreadNotifications(Long receiverId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = 1 WHERE (n.receiverId = :receiverId OR n.receiverId IS NULL) AND n.isRead = 0")
    void markAllAsRead(@Param("receiverId") Long receiverId);

}
