package com.css.coupon_sale.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class OrderRequest {

  private int payment_id;
  private  Integer coupon_id;
  private long user_id;
  private int quantity;
  private  String phoneNumber;
  private int totalPrice;
  private MultipartFile screenshot;
  private List<OrderItemRequest> items;


  public int getPayment_id() {
    return payment_id;
  }

  public void setPayment_id(int payment_id) {
    this.payment_id = payment_id;
  }

  public Integer getCoupon_id() {
    return coupon_id;
  }

  public void setCoupon_id(Integer coupon_id) {
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

  public MultipartFile getScreenshot() {
    return screenshot;
  }

  public void setScreenshot(MultipartFile screenshot) {
    this.screenshot = screenshot;
  }

  public List<OrderItemRequest> getItems() {
    return items;
  }

  public void setItems(List<OrderItemRequest> items) {
    this.items = items;
  }
}
