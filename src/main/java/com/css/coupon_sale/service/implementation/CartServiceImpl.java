package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.CartRequest;
import com.css.coupon_sale.dto.response.CartResponse;
import com.css.coupon_sale.entity.CartEntity;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.CartRepository;
import com.css.coupon_sale.repository.CouponRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final CouponRepository couponRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, CouponRepository couponRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean saveToCart(CartRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new RuntimeException("User Not Found"));
        CouponEntity coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(()-> new RuntimeException("Coupon Not Found!"));
        if (user!=null && coupon !=null){
            Optional<CartEntity> existingCart = cartRepository.findByCoupon_IdAndUser_Id(request.getCouponId(), request.getUserId());
            if (existingCart.isPresent()){
                CartEntity cart = existingCart.get();
                int oldQuantity = cart.getQuantity();
                cart.setQuantity(oldQuantity + request.getQuantity());
                cart.setUpdatedAt(LocalDateTime.now());
                cartRepository.save(cart);
                return true;
            }else {
                CartEntity cartEntity = new CartEntity();
                cartEntity.setUser(user);
                cartEntity.setCoupon(coupon);
                cartEntity.setQuantity(request.getQuantity());
                cartEntity.setStatus(1);
                cartEntity.setCreatedAt(LocalDateTime.now());
                cartRepository.save(cartEntity);
                return true;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean updateQuantity(int cardId, int quantity) {
        CartEntity cart = cartRepository.findById(cardId).orElseThrow(
                ()-> new AppException("Cart Data Not Found", HttpStatus.NOT_FOUND));
        if (cart != null){
            cart.setQuantity(quantity);
            cart.setStatus(1);
            cart.setUpdatedAt(LocalDateTime.now());
            CartEntity updatedCart = cartRepository.save(cart);
            if(updatedCart.getQuantity() == 0){
                updatedCart.setStatus(0);
                updatedCart.setUpdatedAt(LocalDateTime.now()    );
                cartRepository.save(updatedCart);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCart(Integer cardId) {
        try {
            cartRepository.deleteById(cardId);
        }catch (Exception e){
            System.out.println("Error In Delete:" );
            return false;
        }
        return true;
    }

    @Override
    public List<CartResponse> showUserCart(Long userID) {
        List<CartEntity> cartEntities = cartRepository.findByUser_Id(userID);

        return cartEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

    }
    private CartResponse mapToResponseDTO(CartEntity cart) {
        CartResponse responseDTO = modelMapper.map(cart, CartResponse.class);
        responseDTO.setCartId(cart.getId());
        responseDTO.setUserId(cart.getUser().getId());
        responseDTO.setCouponId(cart.getCoupon().getId());
        responseDTO.setQuantity(cart.getQuantity());
        responseDTO.setPrice(cart.getCoupon().getPrice());
        responseDTO.setProductName(cart.getCoupon().getProduct().getName());
        responseDTO.setProductImage(cart.getCoupon().getProduct().getImagePath());
        responseDTO.setExpireDate(cart.getCoupon().getExpiredDate());
        return responseDTO;
    }
}
