package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.MessageRequest;
import com.css.coupon_sale.dto.response.MessageResponse;
import com.css.coupon_sale.dto.response.ReactionResponse;
import com.css.coupon_sale.entity.MessageEntity;
import com.css.coupon_sale.entity.MessageReactionEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.MessageReactionRepository;
import com.css.coupon_sale.repository.MessageRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageReactionRepository messageReactionRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository
            ,MessageReactionRepository messageReactionRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageReactionRepository = messageReactionRepository;
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
    public void editMessage(Long messageId, String newContent) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        message.setContent(newContent);
        message.setEditedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        System.out.println("Here is deleting message from service + id - " + messageId);
        // Delete all reactions associated with the message
        messageReactionRepository.deleteByMessageId(messageId);
        messageRepository.deleteById(messageId);
    }

    @Override
    public List<MessageResponse> getChatMessages(Long userId1, Long userId2) {
        List<MessageEntity> messages = messageRepository.findChatMessages(userId1, userId2);

        return messages.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void addReaction(Long messageId, Long userId, String reaction) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<MessageReactionEntity> existingReaction =
                messageReactionRepository.findByMessageIdAndUserId(messageId, userId);

        if (existingReaction.isPresent()) {
            existingReaction.get().setReaction(reaction);
            messageReactionRepository.save(existingReaction.get());
        } else {
            MessageReactionEntity newReaction = new MessageReactionEntity();
            newReaction.setMessage(message);
            newReaction.setUser(user);
            newReaction.setReaction(reaction);
            messageReactionRepository.save(newReaction);
        }
    }

    @Override
    public void removeReaction(Long messageId, Long userId) {
        messageReactionRepository.findByMessageIdAndUserId(messageId, userId)
                .ifPresent(messageReactionRepository::delete);
    }

    private List<ReactionResponse> getReactions(Long messageId) {
        List<MessageReactionEntity> reactions = messageReactionRepository.findByMessageId(messageId);
        return reactions.stream()
                .map(r -> new ReactionResponse(r.getUser().getId(), r.getReaction()))
                .collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(MessageEntity message) {

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getSendAt(),
                message.getIsRead(),
                message.getEditedAt(),
                getReactions(message.getId())
        );
    }
}