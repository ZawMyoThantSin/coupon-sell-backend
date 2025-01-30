package com.css.coupon_sale.controller;


import com.css.coupon_sale.config.CustomWebSocketHandler;
import com.css.coupon_sale.dto.request.BusinessRequest;
import com.css.coupon_sale.dto.request.PayOwnerRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.request.UpdateBusinessRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.HttpResponse;
import com.css.coupon_sale.dto.response.PayOwnerResponse;
import com.css.coupon_sale.dto.response.SignupResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.PaidHistoryEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.PaidHistoryRepository;
import com.css.coupon_sale.service.BusinessService;
import com.css.coupon_sale.service.PaidHistoryService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaidHistoryService paidHistoryService;

    @Autowired
    private PaidHistoryRepository paidHistoryRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CustomWebSocketHandler webSocketHandler;

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @PostMapping("/add/owner")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest request){
        System.out.println("In REQ:" + request.toString());
        SignupResponse response = businessService.addBusinessOwner(request);
        if (response != null){
            HttpResponse<SignupResponse> res = new HttpResponse<>();
            res.setStatus(true);
            res.setMessage("User Created Successfully");
            res.setData(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }else return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail to Add User");
    }
    @PostMapping
    public ResponseEntity<?> createBusiness(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        System.out.println("Name"+ name);
        System.out.println("Category ID: " + categoryId);
        try {
            BusinessRequest dto = new BusinessRequest();
            dto.setName(name);
            dto.setLocation(location);
            dto.setDescription(description);
            dto.setContactNumber(contactNumber);
            dto.setCategoryId(categoryId);
            dto.setUserId(userId);
            dto.setImage(image);

            BusinessResponse responseDto = businessService.createBusiness(dto);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating business: " + e.getMessage());
        }
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<BusinessResponse> getBusinessByUserId(@PathVariable Long id) {
        try {
            BusinessResponse response = businessService.getByUserId(id);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            System.out.println("Error"+ e.getMessage());
            return ResponseEntity.ok(new BusinessResponse());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getBusinessById(@PathVariable Integer id) {
        BusinessResponse business = businessService.getBusinessById(id);
        return ResponseEntity.ok(business);
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> getAllBusinesses() {
        List<BusinessResponse> businesses = businessService.getAllBusinesses();
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessResponse> updateBusiness(@PathVariable("id") Integer id, @ModelAttribute UpdateBusinessRequest requestDTO) throws IOException{
        BusinessResponse updatedBusiness = businessService.updateBusiness(id, requestDTO);
        return ResponseEntity.ok(updatedBusiness);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBusiness(@PathVariable Integer id) {
        BusinessResponse b = businessService.softDeleteBusiness(id);
        return ResponseEntity.ok("Business deleted successfully");
    }

    @GetMapping("/{id}/income")
    public ResponseEntity<?> getBusinessIncome(@PathVariable Integer id) {
        Double totalIncome = businessService.getTotalIncomeForBusiness(id);
        return ResponseEntity.ok(totalIncome);
    }

    @GetMapping("/{id}/amount-to-pay")
    public ResponseEntity<?> calculateAmountToPay(@PathVariable Integer id) {
        try {
            Double amountToPay = businessService.calculateAmountToPay(id);
            return ResponseEntity.ok(amountToPay);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/pay-owner")
    public ResponseEntity<PayOwnerResponse> payOwner(@RequestBody PayOwnerRequest requestDto) {
        logger.info("Received request to pay owner: {}", requestDto);
        // Validate request
        if (requestDto.getBusinessId() == 0) {
            logger.warn("Invalid business ID: {}", requestDto.getBusinessId());
            return ResponseEntity.badRequest().body(new PayOwnerResponse(0, 0.0, 0.0, 0.0, null, 0.0));
        }
        if (requestDto.getDesiredPercentage() == null) {
            return ResponseEntity.badRequest().body(new PayOwnerResponse(0, 0.0, 0.0, 0.0, null, 0.0));
        }
        if (requestDto.getDesiredPercentage() <= 0 || requestDto.getDesiredPercentage() > 100) {
            return ResponseEntity.badRequest().body(new PayOwnerResponse(0, 0.0, 0.0, 0.0, null, 0.0));
        }

        // Process payment
        try {
            PayOwnerResponse response = paidHistoryService.payOwner(requestDto);
            logger.info("Payment processed successfully: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PayOwnerResponse(0, 0.0, 0.0, 0.0, null,0.0));
        }
    }

    @GetMapping("/update-percent/{id}/{percentage}")
    public ResponseEntity<?> updatePercentage(@PathVariable int id, @PathVariable String percentage){

        Optional<BusinessEntity> business = businessRepository.findById(id);

        if(business != null){
            Long userId = business.get().getUser().getId();
            webSocketHandler.sendToUser(userId, percentage);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{businessId}/paid-history")
    public ResponseEntity<List<PaidHistoryEntity>> getPaidHistory(@PathVariable int businessId) {
        List<PaidHistoryEntity> history = paidHistoryService.getPaidHistory(businessId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/paid-history-report")
    public ResponseEntity<byte[]> generatePaidHistoryReport(
            @RequestParam String reportType,
            @RequestParam(required = false) Integer businessId) throws JRException {
        try {
            byte[] reportBytes = paidHistoryService.generatePaidHistoryReport(businessId, reportType);

            System.out.println("Here is paid history report " + getAllBusinesses().getBody());
            return ResponseEntity.ok().body(reportBytes); // Return only the body
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());

        }
    }
    @GetMapping("/business-report")
    public ResponseEntity<byte[]> generateBusinessReport(

            @RequestParam String reportType,

            HttpServletResponse response
    ) throws JRException {
        // Generate the report
        byte[] reportBytes = businessService.generateBusinessReport( reportType);

        // Set the response headers for file download
        String fileName = "coupon-usage-report." + (reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType(reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.ms-excel");

        return ResponseEntity.ok().body(reportBytes);
    }

    @GetMapping("/business/paid-history/{id}")
    public ResponseEntity<byte[]> generateCustomerReport(
            @PathVariable("id") int businessId,
            @RequestParam String reportFormat) {
        try {
            // Generate the report based on the provided format (PDF or Excel)
            byte[] reportBytes = businessService.generateCustomerReport(businessId,reportFormat);

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportFormat)) {
                headers.set("Content-Disposition", "inline; filename=customer_report.pdf");
                headers.set("Content-Type", "application/pdf");
            } else if ("excel".equalsIgnoreCase(reportFormat)) {
                headers.set("Content-Disposition", "inline; filename=customer_report.xlsx");
                headers.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                return ResponseEntity.badRequest().body("Unsupported report format".getBytes());
            }

            // Return the file as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportBytes);
        } catch (Exception e) {
            // Handle any errors (like JRException or runtime errors)
            return ResponseEntity.status(500).body(("Error generating report: " + e.getMessage()).getBytes());

        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalBusinessCount() {
        long totalBusinessCount = businessRepository.countAllBusinesses();
        long todayBusinessCount = businessRepository.countBusinessesCreatedToday();

        Map<String, Long> response = new HashMap<>();
        response.put("totalBusinessCount", totalBusinessCount);
        response.put("todayBusinessCount", todayBusinessCount);

        return ResponseEntity.ok(response);
    }
}
