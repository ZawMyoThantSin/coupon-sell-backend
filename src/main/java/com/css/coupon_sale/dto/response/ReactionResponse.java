package com.css.coupon_sale.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
public class ReactionResponse {
    private Long userId;
    private String reaction;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public ReactionResponse(Long userId, String reaction) {
        this.userId = userId;
        this.reaction = reaction;
    }
}