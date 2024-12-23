package com.css.coupon_sale.service;

import com.css.coupon_sale.dto.request.CartRequest;
import com.css.coupon_sale.dto.response.CartResponse;
import com.css.coupon_sale.entity.CartEntity;

import java.util.List;

public interface CartService {

    boolean saveToCart(CartRequest request);

    boolean updateQuantity(int cardId, int quantity);

    boolean deleteCart(Integer cardId);

    List<CartResponse> showUserCart(Long userID);
}
