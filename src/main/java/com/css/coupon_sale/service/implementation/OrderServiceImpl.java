package com.css.coupon_sale.service.implementation;


import com.css.coupon_sale.dto.request.OrderRequest;

import com.css.coupon_sale.dto.response.OrderResponse;


import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.PaymentEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.*;
import com.css.coupon_sale.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private UserRepository URepository;

  @Autowired
  private CouponRepository CRepository;

  @Autowired
  private PaymentRepository PRepository;

  @Autowired
  private ModelMapper mapper;

  @Value("${product.image.upload-dir}") // Specify folder path in application.properties
  private String uploadDir;

  @Override
  public OrderResponse saveOrder(OrderRequest request,MultipartFile screenshot) throws IOException {

    UserEntity user = URepository.findById(request.getUser_id()).orElseThrow(
      ()-> new RuntimeException("User Not Found")
    );

    CouponEntity coupon = CRepository.findById(request.getCoupon_id())
      .orElseThrow(() -> new RuntimeException("Coupon not found"));



    PaymentEntity payment  = PRepository.findById(request.getPayment_id()).orElseThrow(
      ()-> new RuntimeException("Payment Not Found")
    );

    OrderEntity order = new OrderEntity();
    order.setCoupon(coupon);
    order.setPayment(payment);
    order.setUser(user);
    order.setTotalPrice(request.getTotalPrice());
    order.setPhoneNumber(request.getPhoneNumber());
    order.setQuantity(request.getQuantity());
    order.setCreatedAt(LocalDateTime.now());


    // Save the uploaded image
    MultipartFile imageFile = request.getScreenshot();
    if (imageFile != null && !imageFile.isEmpty()) {
      System.out.println("IMage is exist");
      String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
      Path filePath = Paths.get(uploadDir+"/order", fileName);
      Files.createDirectories(filePath.getParent());
      Files.write(filePath, imageFile.getBytes());
      order.setScreenshot(fileName); // Save file name/path
    } else {
      order.setScreenshot(""); // Empty if no file uploaded
    }

    OrderEntity orderEntity = orderRepository.save(order);
    OrderResponse response = new OrderResponse();
    response.setId(orderEntity.getId());
    response.setCoupon_id(orderEntity.getCoupon().getId());
    response.setUser_id(orderEntity.getUser().getId());
    response.setPayment_id(orderEntity.getPayment().getId());
    response.setMessage(orderEntity.getMessage());
    response.setPhoneNumber(orderEntity.getPhoneNumber());
    response.setQuantity(orderEntity.getQuantity());
    response.setTotalPrice(orderEntity.getTotalPrice());

//    response.setStatus(orderEntity.isStatus());
    response.setCreatedAt(orderEntity.getCreatedAt());


    response.setScreenshot(orderEntity.getScreenshot());


    return response;
  }

  @Override
  public List<OrderResponse> getByPaymentId(Integer id) {
    List<OrderEntity> orderEntityList = orderRepository.findByPaymentId(id);
    return orderEntityList.stream()
      .map(this::mapToResponseDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<OrderResponse> getAllOrderlist() {
    List<OrderEntity> orders=orderRepository.findAll();

    System.out.println("Orders" + orders);
    List<OrderResponse> response = orders.stream()
      .map(order->mapper.map(orders, OrderResponse.class)).toList();

    return response;
  }

  @Override
  public List<OrderResponse> getByCouponId(Integer id) {
    List<OrderEntity> orderEntityList = orderRepository.findByCouponId(id);
    return orderEntityList.stream()
      .map(this::mapToResponseDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<OrderResponse> getByUserId(long id) {

      List<OrderEntity> orderEntityList = orderRepository.findByUserId(id);
      return orderEntityList.stream()
        .map(this::mapToResponseDTO)
        .collect(Collectors.toList());
  }
  private OrderResponse mapToResponseDTO(OrderEntity order) {
    OrderResponse responseDTO = mapper.map(order, OrderResponse.class);

    return responseDTO;
  }
}
