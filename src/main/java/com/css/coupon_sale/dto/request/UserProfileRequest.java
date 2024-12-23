package com.css.coupon_sale.dto.request;

import org.springframework.web.multipart.MultipartFile;

public class UserProfileRequest {
    private String name;
    private  String phone;
    private String address;
    private MultipartFile profile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MultipartFile getProfile() {
        return profile;
    }

    public void setProfile(MultipartFile profile) {
        this.profile = profile;
    }
}
