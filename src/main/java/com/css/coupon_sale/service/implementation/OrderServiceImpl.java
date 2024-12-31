package com.css.coupon_sale.service.implementation;


import com.css.coupon_sale.dto.request.OrderItemRequest;
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
import java.util.ArrayList;
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

//  @Override
//  public OrderResponse saveOrder(OrderRequest request) throws IOException {
//
//    UserEntity user = URepository.findById(request.getUser_id()).orElseThrow(
//      ()-> new RuntimeException("User Not Found")
//    );
//
//    CouponEntity coupon = CRepository.findById(request.getCoupon_id())
//      .orElseThrow(() -> new RuntimeException("Coupon not found"));
//
//
//
//    PaymentEntity payment  = PRepository.findById(request.getPayment_id()).orElseThrow(
//      ()-> new RuntimeException("Payment Not Found")
//    );
//
//    OrderEntity order = new OrderEntity();
//    order.setCoupon(coupon);
//    order.setPayment(payment);
//    order.setUser(user);
//    order.setTotalPrice(request.getTotalPrice());
//    order.setPhoneNumber(request.getPhoneNumber());
//    order.setQuantity(request.getQuantity());
//    order.setCreatedAt(LocalDateTime.now());
//    order.setStatus(0);
//
//
//    // Save the uploaded image
//    MultipartFile imageFile = request.getScreenshot();
//    if (imageFile != null && !imageFile.isEmpty()) {
//      System.out.println("IMage is exist");
//      String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
//      Path filePath = Paths.get(uploadDir+"/order", fileName);
//      Files.createDirectories(filePath.getParent());
//      Files.write(filePath, imageFile.getBytes());
//      order.setScreenshot(fileName); // Save file name/path
//    } else {
//      order.setScreenshot(""); // Empty if no file uploaded
//    }
//
//    OrderEntity orderEntity = orderRepository.save(order);
//    OrderResponse response = new OrderResponse();
//    response.setId(orderEntity.getId());
//    response.setCoupon_id(orderEntity.getCoupon().getId());
//    response.setUser_id(orderEntity.getUser().getId());
//    response.setPayment_id(orderEntity.getPayment().getId());
//    response.setMessage(orderEntity.getMessage());
//    response.setPhoneNumber(orderEntity.getPhoneNumber());
//    response.setQuantity(orderEntity.getQuantity());
//    response.setTotalPrice(orderEntity.getTotalPrice());
//
//    response.setStatus(orderEntity.getStatus());
//    response.setCreatedAt(orderEntity.getCreatedAt());
//
//
//    response.setScreenshot(orderEntity.getScreenshot());
//
//
//    return response;
//  }

    // Part - 2


//  @Override
//  public OrderResponse saveOrder(OrderRequest request, OrderItemRequest item) throws IOException {
//    // Generate a unique order_id starting from 1000
//    int orderId = generateUniqueOrderId();
//    UserEntity user = URepository.findById(request.getUser_id())
//            .orElseThrow(() -> new RuntimeException("User Not Found"));
//
//    CouponEntity coupon = CRepository.findById(request.getCoupon_id())
//            .orElseThrow(() -> new RuntimeException("Coupon not found"));
//
//    PaymentEntity payment = PRepository.findById(request.getPayment_id())
//            .orElseThrow(() -> new RuntimeException("Payment Not Found"));
//    System.out.println("Processing order for user ID: " + request.getUser_id());
//    System.out.println("Order item details: " + item);
//
//    OrderEntity order = new OrderEntity();
//    order.setCoupon(coupon);
//    order.setPayment(payment);
//    order.setUser(user);
//    order.setTotalPrice(item.getTotalPrice());
//    order.setPhoneNumber(request.getPhoneNumber());
//    order.setQuantity(item.getQuantity());
//    order.setCreatedAt(LocalDateTime.now());
//    order.setOrder_id(orderId); // Assign the generated order_id
//    order.setStatus(0);
//
//    MultipartFile imageFile = request.getScreenshot();
//    if (imageFile != null && !imageFile.isEmpty()) {
//      System.out.println("IMage is exist");
//      String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
//      Path filePath = Paths.get(uploadDir+"/order", fileName);
//      Files.createDirectories(filePath.getParent());
//      Files.write(filePath, imageFile.getBytes());
//      order.setScreenshot(fileName); // Save file name/path
//    } else {
//      order.setScreenshot(""); // Empty if no file uploaded
//    }
//
//    OrderEntity savedOrder = orderRepository.save(order);
//
//    OrderResponse response = new OrderResponse();
//    response.setId(savedOrder.getId());
//    response.setCoupon_id(savedOrder.getCoupon().getId());
//    response.setUser_id(savedOrder.getUser().getId());
//    response.setPayment_id(savedOrder.getPayment().getId());
//    response.setMessage(savedOrder.getMessage());
//    response.setPhoneNumber(savedOrder.getPhoneNumber());
//    response.setQuantity(savedOrder.getQuantity());
//    response.setTotalPrice(savedOrder.getTotalPrice());
//    response.setStatus(savedOrder.getStatus());
//    response.setCreatedAt(savedOrder.getCreatedAt());
//    response.setScreenshot(savedOrder.getScreenshot());
//
//    return response;
//  }

    @Override
    public List<OrderResponse> saveOrders(
            long userId,
            int paymentId,
            String phoneNumber,
            int totalPrice,
            List<Integer> quantities,
            MultipartFile screenshot,
            List<Integer> couponIds
    ) throws IOException {
        // Generate a unique order_id
        int orderId = generateUniqueOrderId();

        // Fetch common entities
        UserEntity user = URepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        PaymentEntity payment = PRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment Not Found"));

        // Store responses
        List<OrderResponse> responses = new ArrayList<>();

        for (int i = 0; i < couponIds.size(); i++) {
            int couponId = couponIds.get(i);
            int quantity = i < quantities.size() ? quantities.get(i) : 1; // Default to 1 if no quantity is provided

            CouponEntity coupon = CRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));


            // Save the order
                OrderEntity order = new OrderEntity();
                order.setOrder_id(orderId); // Use the same order_id
                order.setUser(user);
                order.setPayment(payment);
                order.setCoupon(coupon);
                order.setPhoneNumber(phoneNumber);
                order.setCreatedAt(LocalDateTime.now());
                order.setStatus(0);
                order.setTotalPrice(totalPrice);
                order.setQuantity(quantity);

                // Handle screenshot
                if (screenshot != null && !screenshot.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + screenshot.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + "/order", fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, screenshot.getBytes());
                    order.setScreenshot(fileName);
                } else {
                    order.setScreenshot(""); // Empty if no file uploaded
                }

                OrderEntity savedOrder = orderRepository.save(order);

                // Prepare response
                OrderResponse response = new OrderResponse();
                response.setId(savedOrder.getId());
                response.setOrder_id(savedOrder.getOrder_id());
                response.setCoupon_id(savedOrder.getCoupon() != null ? savedOrder.getCoupon().getId() : null);
                response.setUser_id(savedOrder.getUser().getId());
                response.setPayment_id(savedOrder.getPayment().getId());
                response.setPhoneNumber(savedOrder.getPhoneNumber());
                response.setQuantity(savedOrder.getQuantity());
                response.setTotalPrice(savedOrder.getTotalPrice());
                response.setStatus(savedOrder.getStatus());
                response.setCreatedAt(savedOrder.getCreatedAt());
                response.setScreenshot(savedOrder.getScreenshot());

                responses.add(response);
            }

        return responses;
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
    responseDTO.setCoupon_id(order.getCoupon().getId());
    responseDTO.setPayment_id(order.getPayment().getId());
    responseDTO.setUser_id(order.getUser().getId());

    return responseDTO;
  }

  // Generate a unique order_id starting from 1000
  private int generateUniqueOrderId() {
    int orderId = 1000;

    // Loop until a unique order ID is found
    while (!isOrderIdUnique(orderId)) {
      orderId++; // Increment if the ID already exists
    }

    return orderId;
  }

  private  boolean isOrderIdUnique(int orderId) {
    return orderRepository.findByOrderId(orderId).isEmpty();
  }



}
