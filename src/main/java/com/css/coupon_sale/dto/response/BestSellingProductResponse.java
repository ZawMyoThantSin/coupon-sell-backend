package com.css.coupon_sale.dto.response;

public class BestSellingProductResponse {
    private String productName;
    private Double price;
    private double discount;
    private Double totalPrice;
    private Integer quantity;


    public BestSellingProductResponse(String productName, double price, double discount,double totalPrice, Integer quantity) {
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.quantity = quantity;

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
