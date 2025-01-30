package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(MessageRequest request);
    void editMessage(Long messageId, String newContent);
    void deleteMessage(Long messageId);
    List<MessageResponse> getChatMessages(Long userId1, Long userId2);
    void addReaction(Long messageId, Long userId, String reaction);
    void removeReaction(Long messageId, Long userId);
}