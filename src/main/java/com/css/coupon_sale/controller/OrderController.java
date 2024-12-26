package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.OrderRequest;
import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.OrderResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.OrderService;
import com.css.coupon_sale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  @Value("${product.image.upload-dir}") // Specify folder path in application.properties
  private String uploadDir;

  @Autowired
  private OrderService service;


  @PostMapping                 //create
  public ResponseEntity<OrderResponse> saveOrder(@RequestBody OrderRequest request,@RequestPart(value = "screenshot", required = false) MultipartFile screenshot) throws IOException {

    System.out.println(request.getCoupon_id());
    OrderResponse response = service.saveOrder(request,screenshot);
    if (response != null ){
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.badRequest().build();
  }
  @GetMapping                 //list
  public ResponseEntity<List<OrderResponse>> showAllOrderlist(){
    List<OrderResponse> response =service.getAllOrderlist();
    return  ResponseEntity.ok(response);
  }

  @GetMapping("/p/{id}")
  public ResponseEntity<List<OrderResponse>> getByPayment(@PathVariable("id")Integer id){
    List<OrderResponse> responses = service.getByPaymentId(id);
    if(responses != null){
      return ResponseEntity.ok(responses);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<List<OrderResponse>> getByUserId(@PathVariable int id) {
    return ResponseEntity.ok(service.getByUserId(id));

  }


  @GetMapping("/c/{id}")
  public ResponseEntity<List<OrderResponse>> getByBusiness(@PathVariable("id")Integer id){
    List<OrderResponse> responses = service.getByCouponId(id);
    if(responses != null){
      return ResponseEntity.ok(responses);
    }
    return ResponseEntity.notFound().build();
  }
}
