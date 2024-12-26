package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.OrderRequest;
import com.css.coupon_sale.dto.response.OrderResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

  OrderResponse saveOrder(OrderRequest request, MultipartFile screenshot) throws IOException;

  List<OrderResponse> getByPaymentId(Integer id);
  List<OrderResponse> getAllOrderlist();
  List<OrderResponse> getByCouponId(Integer id);
  List<OrderResponse> getByUserId(long id);

}
