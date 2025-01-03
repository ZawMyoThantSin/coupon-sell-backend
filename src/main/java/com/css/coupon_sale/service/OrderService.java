package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.OrderItemRequest;
import com.css.coupon_sale.dto.request.OrderRequest;
import com.css.coupon_sale.dto.response.OrderDetailResponse;
import com.css.coupon_sale.dto.response.OrderResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

  List<OrderResponse> saveOrders(long userId,
                           int paymentId,
                           String phoneNumber,
                           int totalPrice,
                           List<Integer> quantities,
                           MultipartFile screenshot,
                           List<Integer> couponIds) throws IOException;

  List<OrderResponse> getByPaymentId(Integer id);
  List<OrderResponse> getAllOrderlist();
  List<OrderResponse> getByCouponId(Integer id);
  List<OrderResponse> getByUserId(long id);

  List<OrderDetailResponse> getByOrderId(int id);
  boolean acceptOrder(int orderId,String action);
}
