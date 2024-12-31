package com.css.coupon_sale.dto.response;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class OrderResponse {
  private int id;
  private int payment_id;
  private  int coupon_id;
  private  long user_id;
  private int quantity;
  private  String phoneNumber;
  private int totalPrice;
  private String screenshot;
  private LocalDateTime createdAt;
  private String updatedAt;
  private String message;
  private int status;
  private int order_id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPayment_id() {
    return payment_id;
  }

  public void setPayment_id(int payment_id) {
    this.payment_id = payment_id;
  }

  public int getCoupon_id() {
    return coupon_id;
  }

  public void setCoupon_id(int coupon_id) {
    this.coupon_id = coupon_id;
  }

  public long getUser_id() {
    return user_id;
  }

  public void setUser_id(long user_id) {
    this.user_id = user_id;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public int getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(int totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getScreenshot() {
    return screenshot;
  }

  public void setScreenshot(String screenshot) {
    this.screenshot = screenshot;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getOrder_id() {
    return order_id;
  }

  public void setOrder_id(int order_id) {
    this.order_id = order_id;
  }
}
