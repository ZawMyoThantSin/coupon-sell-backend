package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;
import com.css.coupon_sale.entity.MessageEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.MessageRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MessageResponse sendMessage(MessageRequest request) {
        UserEntity sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        UserEntity receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        MessageEntity message = new MessageEntity();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setSendAt(LocalDateTime.now());

        MessageEntity savedMessage = messageRepository.save(message);

        return mapToResponse(savedMessage);
    }

    @Override
    public List<MessageResponse> getChatMessages(Long userId1, Long userId2) {
        List<MessageEntity> messages = messageRepository.findChatMessages(userId1, userId2);

        return messages.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(MessageEntity message) {

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getSendAt()
        );
    }
}
