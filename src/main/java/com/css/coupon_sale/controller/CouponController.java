package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.response.*;
import com.css.coupon_sale.entity.CouponValidationEntity;
import com.css.coupon_sale.service.CouponService;
import com.css.coupon_sale.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductService pservice;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/calculate/{id}")
    public ResponseEntity<Double> calculateCouponPrice(@PathVariable("id") Integer ProductId) {
        try {
            ProductResponse product = pservice.showProductbyId(ProductId);
//            double discountedPrice = CouponService.calculateDiscountedPrice(productId, couponCode);
            double originPrice = product.getPrice();
            float discountPrice = product.getDiscount();

//            double rate = discountPrice / 100.0;
            double f = originPrice * (discountPrice / 100.0);
            System.out.println(f);
            double FinalPrice = originPrice - f;
            System.out.println(FinalPrice);
            return ResponseEntity.ok(FinalPrice);
        } catch (IllegalArgumentException e) {
            System.out.println("ERRor in Calculate"+ e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest requestDTO) {
        // Call service with the BusinessRequest DTO
        CouponResponse savedCoupon = couponService.saveCoupon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Integer id) {
        CouponResponse business = couponService.getCouponById(id);
        return ResponseEntity.ok(business);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Boolean> increaseViewCount(@PathVariable Integer id) {
        couponService.increaseViewCount(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupon() {
        List<CouponResponse> businesses = couponService.getAllCouponlist();
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Integer id, @RequestBody CouponRequest
            requestDTO) {
        CouponResponse updatedCoupon = couponService.updateCoupon(id, requestDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    @GetMapping("/b/{id}")
    public ResponseEntity<List<CouponResponse>> getByCoupon(@PathVariable("id")Integer id){
        List<CouponResponse> responses = couponService.showByBusinessId(id);
        if(responses != null){
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/weeklyRreport/{id}")
    public ResponseEntity<byte[]> saleCouponReportForWeekly(@PathVariable("id") Integer businessId, @RequestParam String reportType) {
        try {

            // Validate inputs
            if (businessId == null || reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            byte[] reportBytes = couponService.saleCouponReportForWeekly(businessId,reportType);

            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=product_list.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=product_list.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0,pre-check=0");

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




    @GetMapping("/monthlyReport/{id}")
    public ResponseEntity<byte[]> saleCouponReportForMonthly(@PathVariable("id") Integer businessId, @RequestParam String reportType) {
        try {

            // Validate inputs
            if (businessId == null || reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            byte[] reportBytes = couponService.saleCouponReportForMonthly(businessId,reportType);

            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=product_list.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=product_list.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0,pre-check=0");

            return ResponseEntity.ok().headers(headers).body(reportBytes);
        } catch (JRException  e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }
    @GetMapping("/usages/{businessId}")
    public ResponseEntity<List<CouponUsedResponse>> getAllCouponUsages(@PathVariable Integer businessId) {
        List<CouponUsedResponse> usages = couponService.getAllCouponUsages(businessId);
        return ResponseEntity.ok(usages);
    }


    @GetMapping("/coupon-usage/weekly/{businessId}")
    public ResponseEntity<byte[]> generateCouponUsageReportweekly(
            @PathVariable Integer businessId,
            @RequestParam String reportType,

            HttpServletResponse response
    ) throws JRException {
        // Generate the report
        byte[] reportBytes = couponService.generateCouponUsageReportweekly(businessId, reportType);

        // Set the response headers for file download
        String fileName = "coupon-usage-report." + (reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType(reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.ms-excel");

        return ResponseEntity.ok().body(reportBytes);
    }


    @GetMapping("/coupon-usage/monthly/{businessId}")
    public ResponseEntity<byte[]> generateCouponUsageReportmonthly(
            @PathVariable Integer businessId,
            @RequestParam String reportType,

            HttpServletResponse response
    ) throws JRException {
        // Generate the report
        byte[] reportBytes = couponService.generateCouponUsageReportmonthly(businessId, reportType);

        // Set the response headers for file download
        String fileName = "coupon-usage-report." + (reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType(reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.ms-excel");

        return ResponseEntity.ok().body(reportBytes);
    }

    @GetMapping("/coupon-report/{businessId}")
    public ResponseEntity<byte[]> generateCouponReport(
            @PathVariable Integer businessId,
            @RequestParam String reportType,

            HttpServletResponse response
    ) throws JRException {
        // Generate the report
        byte[] reportBytes = couponService.generateCouponReport(businessId, reportType);

        // Set the response headers for file download
        String fileName = "coupon-usage-report." + (reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType(reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.ms-excel");

        return ResponseEntity.ok().body(reportBytes);
    }

    @GetMapping("/report")
    public ResponseEntity<?> generateCouponUsageReport(
            @RequestParam String reportType,
            @RequestParam Integer businessId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // Validate inputs
            if (reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }

            // Fetch coupon usages
            List<CouponUsed2Response> couponUsages = couponService.getAllCoupon(businessId);

            // Debugging: Print startDate, endDate, and coupon usage timestamps
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            couponUsages.forEach(couponUsage -> {
                System.out.println("Coupon Usage: " + couponUsage.getUsedAt() + ", Used At: " + couponUsage.getUsedAt());
            });

            // Filter coupon usages based on date range
            if (startDate != null && endDate != null) {
                couponUsages = couponUsages.stream()
                        .filter(couponUsage -> {
                            Date usedAt = couponUsage.getUsedAt();
                            if (usedAt == null) {
                                return false;
                            }
                            // Convert java.util.Date to java.time.LocalDateTime
                            LocalDateTime usedAtLocalDateTime = usedAt.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            return !usedAtLocalDateTime.isBefore(startDate) && !usedAtLocalDateTime.isAfter(endDate);
                        })
                        .collect(Collectors.toList());
            }

            // Generate report bytes
            byte[] reportBytes = couponService.generateCouponUsageReport(couponUsages, reportType);

            // Set headers based on report type
            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=coupon_usage_report.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=coupon_usage_report.xlsx");
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

    // Method to map CouponValidationEntity to CouponUsageResoponse
    private CouponUsed2Response mapToUsageCouponListResponse(CouponValidationEntity coupon) {
        CouponUsed2Response response = modelMapper.map(coupon, CouponUsed2Response.class);
        LocalDateTime localDateTime = coupon.getUsedAt();
        if (localDateTime != null) {
            // Convert LocalDateTime to Date
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            response.setUsedAt(date);
        } else {
            response.setUsedAt(null); // Handle null case
        }
        return response;
    }

    @GetMapping("/coupon-sales-report")
    public ResponseEntity<?> generateCouponSalesReport(
            @RequestParam String reportType,
            @RequestParam Integer businessId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // Validate inputs
            if (reportType == null || reportType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request parameters".getBytes());
            }
            // Fetch coupon sales data
            List<CouponSalesBusinessResponse2> couponSales = couponService.getAllCouponSales(businessId);

            // Debugging: Print startDate, endDate, and coupon sales timestamps
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            couponSales.forEach(couponSale -> {
                System.out.println("Coupon Sale: " + couponSale.getBuyDate() + ", Sold At: " + couponSale.getBuyDate());
            });
            // Filter coupon sales based on date range
            if (startDate != null && endDate != null) {
                couponSales = couponSales.stream()
                        .filter(couponSale -> {
                            Date buyDate = couponSale.getBuyDate();
                            if (buyDate == null) {
                                return false;
                            }
                            // Convert java.util.Date to java.time.LocalDateTime
                            LocalDateTime buyDateLocalDateTime = buyDate.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            return !buyDateLocalDateTime.isBefore(startDate) && !buyDateLocalDateTime.isAfter(endDate);
                        })
                        .collect(Collectors.toList());
            }

            // Generate report bytes
            byte[] reportBytes = couponService.generateCouponSalesReport(couponSales, reportType);

            // Set headers based on report type
            HttpHeaders headers = new HttpHeaders();
            if ("pdf".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=coupon_sales_report.pdf");
            } else if ("excel".equalsIgnoreCase(reportType)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=coupon_sales_report.xlsx");
            } else {
                return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok().headers(headers).body(reportBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/remaining-coupon-report/{businessId}")
    public ResponseEntity<byte[]> generateRemainingCouponReport(
            @PathVariable Integer businessId,   // Dynamic businessId passed as a path variable
            @RequestParam String reportType,    // Report type as query parameter (pdf or excel)
            HttpServletResponse response
    ) throws JRException {
        // Validate reportType
        if (reportType == null || (!reportType.equalsIgnoreCase("pdf") && !reportType.equalsIgnoreCase("excel"))) {
            // Return bad request response if reportType is invalid or missing
            return ResponseEntity.badRequest().body("Missing or invalid reportType parameter. Valid values are 'pdf' or 'excel'.".getBytes());
        }

        // Generate the remaining coupon report using the service method
        byte[] reportBytes = couponService.generateRemainingCouponReport(businessId, reportType);

        // Determine the file extension based on reportType
        String fileExtension = reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx";
        String fileName = "remaining-coupon-report-" + businessId + "." + fileExtension;

        // Set response headers for file download
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType(reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.ms-excel");

        // Return the generated report as a byte array in the response body
        return ResponseEntity.ok().body(reportBytes);
    }
    @GetMapping("/best-selling-product-report/{businessId}")
    public ResponseEntity<byte[]> generateBestSellingProductReport(
            @PathVariable Integer businessId,  // Business ID as a path variable
            @RequestParam String reportType,   // Report type as a query parameter (pdf or excel)
            HttpServletResponse response       // HTTP response object
    ) {
        // Validate the reportType
        if (reportType == null || (!reportType.equalsIgnoreCase("pdf") && !reportType.equalsIgnoreCase("excel"))) {
            return ResponseEntity.badRequest()
                    .body("Missing or invalid reportType parameter. Valid values are 'pdf' or 'excel'.".getBytes());
        }

        try {
            // Call the service method to generate the report
            byte[] reportBytes = couponService.generateBestSellingProductReport(businessId, reportType);

            // Determine file extension and content type based on report type
            String fileExtension = reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx";
            String contentType = reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            String fileName = "best-selling-product-report-" + businessId + "." + fileExtension;

            // Set the response headers for file download
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType(contentType);

            // Return the generated report as a byte array in the response body
            return ResponseEntity.ok().body(reportBytes);

        } catch (RuntimeException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/expired-coupon-report/{businessId}")
    public ResponseEntity<byte[]> generateExpiredCouponReport(
            @PathVariable Integer businessId,  // Business ID as a path variable
            @RequestParam String reportType,   // Report type as a query parameter (pdf or excel)
            HttpServletResponse response       // HTTP response object
    ) {
        // Validate the reportType
        if (reportType == null || (!reportType.equalsIgnoreCase("pdf") && !reportType.equalsIgnoreCase("excel"))) {
            return ResponseEntity.badRequest()
                    .body("Missing or invalid reportType parameter. Valid values are 'pdf' or 'excel'.".getBytes());
        }

        try {
            // Call the service method to generate the expired coupon report
            byte[] reportBytes = couponService.generateExpiredCouponDataReport(businessId, reportType);

            // Determine file extension and content type based on report type
            String fileExtension = reportType.equalsIgnoreCase("pdf") ? "pdf" : "xlsx";
            String contentType = reportType.equalsIgnoreCase("pdf") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            String fileName = "expired-coupon-report-" + businessId + "." + fileExtension;

            // Set the response headers for file download
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType(contentType);

            // Return the generated report as a byte array in the response body
            return ResponseEntity.ok().body(reportBytes);

        } catch (RuntimeException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
}
