package com.css.coupon_sale.service.implementation;


import com.css.coupon_sale.dto.request.OrderItemRequest;
import com.css.coupon_sale.dto.request.OrderRequest;

import com.css.coupon_sale.dto.response.OrderDetailResponse;
import com.css.coupon_sale.dto.response.OrderResponse;


import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.PaymentEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.*;
import com.css.coupon_sale.service.OrderService;
import com.css.coupon_sale.service.SaleCouponService;
import org.hibernate.query.Order;
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
import java.util.Map;
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
  private SaleCouponService saleCouponService;

  @Autowired
  private ModelMapper mapper;

  @Value("${product.image.upload-dir}") // Specify folder path in application.properties
  private String uploadDir;

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
                order.setOrderId(orderId); // Use the same order_id
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
                response.setOrder_id(savedOrder.getOrderId());
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
        // Filter to retain only one entry per order_id

      Map<Integer, OrderEntity> uniqueOrders = orders.stream()
              .collect(Collectors.toMap(
                      OrderEntity::getOrderId,  // Key: order_id
                      order -> order,          // Value: order entity
                      // Default: Keep the first occurrence
                      (existing, replacement) -> existing
                      // Uncomment below to keep the latest order based on createdAt
                      //(existing, replacement) -> existing.getCreatedAt().isAfter(replacement.getCreatedAt()) ? existing : replacement
              ));

      // Convert the unique orders to response DTOs
      return uniqueOrders.values().stream()
              .map(this::mapToResponseDTO)
              .collect(Collectors.toList());
//      return orders.stream()
//              .map(this::mapToResponseDTO)
//              .collect(Collectors.toList());
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

    @Override
    public List<OrderDetailResponse> getByOrderId(int id) {
        try {
            List<OrderEntity> orderEntityList = orderRepository.findByOrderId(id);
            List<OrderDetailResponse> responses = new ArrayList<>();

            if (!orderEntityList.isEmpty()) {
                OrderEntity order = orderEntityList.get(0); // Get the first order entity
                OrderDetailResponse response = new OrderDetailResponse();

                // Set fields from the first order entity
                response.setId(order.getId());
                response.setOrder_id(order.getOrderId());
                response.setUser_id(order.getUser().getId());
                response.setQuantity(order.getQuantity());
                response.setTotalPrice(order.getTotalPrice());
                response.setStatus(order.getStatus());
                response.setScreenshot(order.getScreenshot());
                response.setOrder_date(order.getCreatedAt());
                response.setMessage(order.getMessage());

                // Set user details
                response.setUserName(order.getUser().getName());
                response.setPhoneNumber(order.getPhoneNumber());
                response.setUserEmail(order.getUser().getEmail());

                // Map payment info
                OrderDetailResponse.PaymentInfo paymentInfo = new OrderDetailResponse.PaymentInfo(
                        order.getPayment().getAccountName(),
                        order.getPayment().getAccountNumber(),
                        order.getPayment().getPaymentType()
                );
                response.setPaymentInfo(paymentInfo);

                // Loop through the items (coupons) and set the order items
                List<OrderDetailResponse.OrderItems> orderItems = new ArrayList<>();
                for (OrderEntity orderEntity : orderEntityList) {
                    OrderDetailResponse.OrderItems orderItem = new OrderDetailResponse.OrderItems(
                            orderEntity.getCoupon().getProduct().getName(),
                            orderEntity.getCoupon().getProduct().getImagePath(),
                            orderEntity.getCoupon().getPrice(),
                            orderEntity.getQuantity()
                    );
                    orderItems.add(orderItem);
                }

                response.setOrderItems(orderItems);  // Set the order items list

                // Add the populated response object to the responses list
                responses.add(response);
            }

            return responses;
        }catch (Exception e){
            System.out.println("IN ODSRV :"+e.getMessage());
        }
        return List.of();
    }

    @Override
    public boolean updateOrderStatus(int orderId,String action) {
        try {
            List<OrderEntity> orderEntityList = orderRepository.findByOrderId(orderId);
            if(!orderEntityList.isEmpty()){
                if(action.equals("ACCEPT")){
                    for (OrderEntity order : orderEntityList){
                        order.setStatus(1);
                        orderRepository.save(order);
                    }
                    boolean status = saleCouponService.insertSaleCoupon(orderId);
                    System.out.println("INSERT Success? "+ status);
                }else if (action.equals("REJECT")){
                    for (OrderEntity order : orderEntityList){
                        order.setStatus(2);
                        orderRepository.save(order);
                    }
                }
                return true;
            }
        }catch (Exception e){
            System.out.println("ERROR IN ORSRV: " + e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public int getOrderCountWithStatusZero() {
        return orderRepository.countByStatus(0);
    }

    private OrderResponse mapToResponseDTO(OrderEntity order) {
    OrderResponse responseDTO = mapper.map(order, OrderResponse.class);
    responseDTO.setOrder_id(order.getOrderId());
    responseDTO.setUserName(order.getUser().getName());
    responseDTO.setUserEmail(order.getUser().getEmail());
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
