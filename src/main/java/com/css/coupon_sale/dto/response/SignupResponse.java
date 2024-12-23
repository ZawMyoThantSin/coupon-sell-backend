package com.css.coupon_sale.dto.response;

import java.time.LocalDateTime;

public class SignupResponse{
    private LocalDateTime inserted_at;
    private Long id;
    private String name;
    private String email;


    public SignupResponse() {
    }

    public SignupResponse(LocalDateTime inserted_at, Long id, String name, String email) {
        this.inserted_at = inserted_at;
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public LocalDateTime getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(LocalDateTime inserted_at) {
        this.inserted_at = inserted_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
