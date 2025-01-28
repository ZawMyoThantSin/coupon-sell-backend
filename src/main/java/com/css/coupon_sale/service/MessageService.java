package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(MessageRequest request);
    List<MessageResponse> getChatMessages(Long userId1, Long userId2);
}
