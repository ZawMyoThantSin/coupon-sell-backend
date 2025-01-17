package com.css.coupon_sale.dto.response;

import java.time.LocalDateTime;

public class OwnerOrderResponse {

  private String userName;
  private String userEmail;
  private String productName;
  private int quantity;
  private double totalPrice;
  private LocalDateTime orderDate;
 private String productPhoto;
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public String getProductPhoto() {
    return productPhoto;
  }

  public void setProductPhoto(String productPhoto) {
    this.productPhoto = productPhoto;
  }
}
