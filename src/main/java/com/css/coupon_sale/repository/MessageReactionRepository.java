package com.css.coupon_sale.repository;

import com.css.coupon_sale.entity.MessageReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReactionEntity, Long> {
    List<MessageReactionEntity> findByMessageId(Long messageId);
    Optional<MessageReactionEntity> findByMessageIdAndUserId(Long messageId, Long userId);
    @Modifying
    @Query(value = "DELETE FROM message_reactions WHERE message_id = :messageId", nativeQuery = true)
    void deleteByMessageId(@Param("messageId") Long messageId);
}