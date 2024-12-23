package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.CartRequest;
import com.css.coupon_sale.dto.response.CartResponse;
import com.css.coupon_sale.entity.CartEntity;
import com.css.coupon_sale.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart/")
public class CartController {
    private final CartService cartService;


    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
//    Get Cart Data By userID
    @GetMapping("/{id}")
    public ResponseEntity<List<CartResponse>> getAllCartByUser(@PathVariable Long id){
        List<CartResponse> cartEntities = cartService.showUserCart(id);
        return ResponseEntity.ok(cartEntities);
    }

    @PostMapping("/qty/{id}")
    public ResponseEntity<Boolean> updateQuantity(@PathVariable("id") int id,@RequestBody Quantity qty){
        Boolean success = cartService.updateQuantity(id,qty.qty);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean>  addToCart(@RequestBody CartRequest request){
        boolean status = cartService.saveToCart(request);
        return status ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCart(@PathVariable("id")Integer id){
        boolean status  = cartService.deleteCart(id);
        if(status){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.internalServerError().build();
    }

    private record Quantity(Integer qty){}

}
