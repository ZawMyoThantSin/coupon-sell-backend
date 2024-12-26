package com.css.coupon_sale.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BusinessCategoryRequest {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
