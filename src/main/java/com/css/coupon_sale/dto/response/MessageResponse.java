package com.css.coupon_sale.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sendAt;
    private Integer isRead;
    private LocalDateTime editedAt;
    private List<ReactionResponse> reactions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public List<ReactionResponse> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionResponse> reactions) {
        this.reactions = reactions;
    }

    public MessageResponse(Long id, Long senderId, Long receiverId, String content, LocalDateTime sendAt, Integer isRead, LocalDateTime editedAt, List<ReactionResponse> reactions) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sendAt = sendAt;
        this.isRead = isRead;
        this.editedAt = editedAt;
        this.reactions = reactions;
    }
}