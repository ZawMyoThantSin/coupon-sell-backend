package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.CustomerResponse;
import com.css.coupon_sale.dto.response.UserListResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.UserProfileService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class AdminController {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final UserProfileService userProfileService;

    @Autowired
    public AdminController(UserRepository userRepository, ModelMapper modelMapper, UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userProfileService = userProfileService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllCustomers(){
        List<UserEntity> userEntities = userRepository.findAll();
        List<CustomerResponse> responses = userEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private CustomerResponse mapToResponseDTO(UserEntity user) {
        return modelMapper.map(user, CustomerResponse.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> editUserFund(@PathVariable("id")Long id, @RequestBody String fund){
        UserEntity user  =  userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user ==null){
            return ResponseEntity.notFound().build();
        }
        user.setFunds(Integer.parseInt(fund));
        UserEntity updatedUser =  userRepository.save(user);
        CustomerResponse response = mapToResponseDTO(updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    public ResponseEntity<?> generateCustomerListReport(
            @RequestParam String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // Validate inputs
            if (reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            // Fetch all customers
            List<UserEntity> userEntities = userRepository.findAll();

            // Debugging: Print startDate, endDate, and user created_at timestamps
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            userEntities.forEach(user -> {
                System.out.println("User: " + user.getName() + ", Created At: " + user.getCreated_at());
            });

            // Filter users based on date range
            if (startDate != null && endDate != null) {
                userEntities = userEntities.stream()
                        .filter(user -> user.getCreated_at() != null &&
                                !user.getCreated_at().isBefore(startDate) &&
                                !user.getCreated_at().isAfter(endDate))
                        .collect(Collectors.toList());
            }

            // Map UserEntity to UserListResponse
            List<UserListResponse> userListResponses = userEntities.stream()
                    .map(this::mapToUserListResponse)
                    .collect(Collectors.toList());

            // Create JRBeanCollectionDataSource from the list of UserListResponse
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userListResponses);

            // Generate report bytes
            byte[] reportBytes = userProfileService.generateCustomerListReport(userListResponses, reportType);

            // Set headers based on report type
            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=customer_list.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=customer_list.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok().headers(headers).body(reportBytes);
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }




    // Method to map UserEntity to UserListResponse
    private UserListResponse mapToUserListResponse(UserEntity user) {
        UserListResponse response = modelMapper.map(user, UserListResponse.class);
        LocalDateTime localDateTime = user.getCreated_at();
        if (localDateTime != null) {
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            response.setCreated_at(date);
        } else {
            response.setCreated_at(null); // Handle null case
        }
        return response;
    }

}
