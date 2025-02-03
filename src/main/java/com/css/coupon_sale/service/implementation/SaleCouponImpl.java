package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.response.BusinessEarningsDTO;
import com.css.coupon_sale.dto.response.PurchaseCouponResponse;
import com.css.coupon_sale.entity.OrderEntity;
import com.css.coupon_sale.entity.QrCodeEntity;
import com.css.coupon_sale.entity.SaleCouponEntity;
import com.css.coupon_sale.repository.OrderRepository;
import com.css.coupon_sale.repository.QrCodeRepository;
import com.css.coupon_sale.repository.SaleCouponRepository;
import com.css.coupon_sale.service.QrCodeService;
import com.css.coupon_sale.service.SaleCouponService;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleCouponImpl implements SaleCouponService {
    @Autowired
    private SaleCouponRepository saleCouponRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QrCodeService qrCodeService;

    @Override
    public boolean insertSaleCoupon(int orderId) throws IOException {
        try {
            // Fetch OrderEntity by orderId
            List<OrderEntity> orderEntityList = orderRepository.findByOrderId(orderId);
            if (orderEntityList.isEmpty()) {
                throw new IllegalArgumentException("Order not found for ID: " + orderId);
            }

            // Loop through each OrderEntity and create corresponding SaleCouponEntities
            for (OrderEntity order : orderEntityList) {
                int quantity = order.getQuantity(); // Get the quantity from the order

                for (int i = 0; i < quantity; i++) { // Loop for each unit of the quantity
                    SaleCouponEntity saleCoupon = new SaleCouponEntity();
                    saleCoupon.setBusiness(order.getCoupon().getProduct().getBusiness());
                    saleCoupon.setUser(order.getUser());
                    saleCoupon.setCoupon(order.getCoupon());
                    saleCoupon.setPayment(order.getPayment());
                    saleCoupon.setQuantity(1);
                    saleCoupon.setStatus(0);
                    saleCoupon.setTotalPrice(order.getCoupon().getPrice()); // Assuming totalPrice = price of one coupon
                    saleCoupon.setBuyDate(order.getCreatedAt());
                    saleCoupon.setExpiredDate(order.getCoupon().getExpiredDate()); // Example expiration logic


                    // Save SaleCouponEntity to the database
                    SaleCouponEntity savedCoupon = saleCouponRepository.save(saleCoupon);
                    if (savedCoupon!=null) {
//       QR GENERATE
                        boolean status = qrCodeService.createAndSaveQrCode(savedCoupon.getId());
                    }else {
                        System.out.println("Error In Save Sale Coupon:");
                    }

                }
            }
            return true; // Return true if processing is successful
        } catch (Exception e) {
            System.out.println("Error inserting Sale Coupons: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<PurchaseCouponResponse> getAllCouponsByUserId(Long userId) {
        List<SaleCouponEntity> saleCouponEntities=saleCouponRepository.findByUser_Id(userId);
        List<PurchaseCouponResponse> response=new ArrayList<>();
        for (SaleCouponEntity coupon : saleCouponEntities) {
            PurchaseCouponResponse purchaseCoupon =new PurchaseCouponResponse();
            purchaseCoupon.setBusinessId(coupon.getBusiness().getId());
            purchaseCoupon.setBusinessName(coupon.getBusiness().getName());
            purchaseCoupon.setBusinessLocation(coupon.getBusiness().getLocation());
            purchaseCoupon.setSaleCouponId(coupon.getId());
            purchaseCoupon.setStatus(coupon.getStatus());
            purchaseCoupon.setDiscount(coupon.getCoupon().getProduct().getDiscount());
            purchaseCoupon.setPrice(coupon.getTotalPrice());
            purchaseCoupon.setExpiryDate(coupon.getExpiredDate());
            purchaseCoupon.setProductName(coupon.getCoupon().getProduct().getName());
            purchaseCoupon.setImageUrl(coupon.getCoupon().getProduct().getImagePath());
            response.add(purchaseCoupon);

        }

        return response;
    }

    @Override
    public PurchaseCouponResponse getBySaleCouponId(int id) {
        SaleCouponEntity saleCoupon = saleCouponRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("SaleCoupon Not Found"));
        if (saleCoupon != null){
            PurchaseCouponResponse response = new PurchaseCouponResponse();
            response.setBusinessName(saleCoupon.getBusiness().getName());
            response.setBusinessId(saleCoupon.getBusiness().getId());
            response.setBusinessLocation(saleCoupon.getBusiness().getLocation());
            response.setSaleCouponId(saleCoupon.getId());
            response.setProductName(saleCoupon.getCoupon().getProduct().getName());
            response.setStatus(saleCoupon.getStatus());
            response.setExpiryDate(saleCoupon.getExpiredDate());
            response.setDiscount(saleCoupon.getCoupon().getProduct().getDiscount());
            response.setImageUrl(saleCoupon.getCoupon().getProduct().getImagePath());
            return  response;
        }
        return null;
    }

    @Override
    public List<BusinessEarningsDTO> getBusinessEarnings() {
        List<Object[]> results = saleCouponRepository.groupTotalEarningsByBusinessId();
        return results.stream()
                .map(row -> new BusinessEarningsDTO((Integer) row[0], (Double) row[2],(String) row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public BusinessEarningsDTO getEarningsByBusinessId(int businessId) {
        try {
            List<Object[]> results = saleCouponRepository.findTotalEarningsByBusinessId(businessId);

            if (!results.isEmpty()) {
                Object[] row = results.get(0);
//                System.out.println("Row: " + Arrays.toString(row));

                Integer businessIdResult = ((Number) row[0]).intValue();
                Double totalEarnings = ((Number) row[2]).doubleValue();
                String businessName = (String) row[1];
                return new BusinessEarningsDTO(businessIdResult, totalEarnings,businessName);
            } else {
                throw new EntityNotFoundException("No earnings found for business ID " + businessId);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Double getCurrentMonthEarnings(int businessId) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the start of the current month
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        // Calculate the start of the next month
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        // Call repository method to fetch earnings
        return saleCouponRepository.findMonthlyEarningsByBusinessIdAndMonth(
                businessId, startOfMonth, startOfNextMonth);
    }

    @Override
    public Double getCurrentYearEarnings(int businessId) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the start of the current year
        LocalDateTime startOfYear = today.withDayOfYear(1).atStartOfDay();

        // Calculate the start of the next year
        LocalDateTime startOfNextYear = startOfYear.plusYears(1);

        // Call repository method to fetch earnings
        return saleCouponRepository.findYearlyEarningsByBusinessIdAndYear(
                businessId, startOfYear, startOfNextYear);
    }

    @Override
    public Map<String, Double> getExistingMonthsWithEarnings(int businessId) {
        List<Object[]> results = saleCouponRepository.findMonthlyEarningsByBusinessId(businessId);
        Map<String, Double> monthEarningsMap = new LinkedHashMap<>(); // Maintain order

        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            double earnings = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            String formattedMonth = String.format("%04d-%02d", year, month);
            monthEarningsMap.put(formattedMonth, earnings);
        }
        return monthEarningsMap;
    }


}
