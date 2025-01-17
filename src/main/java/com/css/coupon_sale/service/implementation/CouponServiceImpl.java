package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.BusinessCouponSalesResponse;
import com.css.coupon_sale.dto.response.CouponResponse;
import com.css.coupon_sale.dto.response.CouponSalesBusinessResponse;
import com.css.coupon_sale.dto.response.CouponUsedResponse;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.ProductEntity;
import com.css.coupon_sale.repository.CouponRepository;
import com.css.coupon_sale.repository.CouponValidationRepository;
import com.css.coupon_sale.repository.ProductRepository;
import com.css.coupon_sale.service.CouponService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    private final ProductRepository productRepository;
    private final CouponValidationRepository couponValidationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository, ProductRepository productRepository, ModelMapper modelMapper,CouponValidationRepository couponValidationRepository) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.couponValidationRepository=couponValidationRepository;
    }

    @Override
    public CouponResponse getCouponById(Integer id) {
        CouponEntity business = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponseDTO(business);
    }

    @Override
    public List<CouponResponse> getAllCouponlist() {
        return  couponRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse updateCoupon(Integer id, CouponRequest requestDTO) {
        CouponEntity coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        ProductEntity product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        coupon.setProduct(product);
        coupon.setCouponCode(requestDTO.getCouponCode());
        coupon.setDescription(requestDTO.getDescription());
        coupon.setPrice(requestDTO.getPrice());

        LocalDate localDate = LocalDate.parse(requestDTO.getExpiredDate());
        LocalTime localTime = LocalTime.now();
        LocalDateTime exp = localDate.atTime(localTime);

        coupon.setExpiredDate(exp);
        coupon.setQuantity(requestDTO.getQuantity());
        coupon.setCreatedAt(LocalDateTime.now());

        CouponEntity updatedBusiness = couponRepository.save(coupon);
        return mapToResponseDTO(updatedBusiness);
    }

    @Override
    public CouponResponse saveCoupon(CouponRequest requestDTO) {
        System.out.println("PID: "+requestDTO.getProductId());
        ProductEntity product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        System.out.println("REQ: "+ requestDTO.toString());
        CouponEntity coupon = new CouponEntity();
        coupon.setProduct(product);
        coupon.setCouponCode(requestDTO.getCouponCode());
        coupon.setDescription(requestDTO.getDescription());
        coupon.setPrice(requestDTO.getPrice());
        LocalDate localDate = LocalDate.parse(requestDTO.getExpiredDate());
        LocalTime localTime = LocalTime.now();
        LocalDateTime exp = localDate.atTime(localTime);

        coupon.setVisible(true);
        coupon.setExpiredDate(exp);
        coupon.setQuantity(requestDTO.getQuantity());
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setCouponRemain(requestDTO.getQuantity());
        CouponEntity savedBusiness = couponRepository.save(coupon);
        return mapToResponseDTO(savedBusiness);
    }

    @Override
    public List<CouponResponse> showByBusinessId(Integer id) {
        List<CouponEntity> productEntities = couponRepository.findByProduct_BusinessId(id);
        return productEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessCouponSalesResponse> getSoldCouponCountByBusiness(Integer businessId) {
        try {
            // Fetch results from the repository
            List<Object[]> results = couponRepository.getSoldCouponCountByBusiness(businessId);

            // Handle empty results gracefully
            if (results == null || results.isEmpty()) {
                System.out.println("No coupons found for business ID: " + businessId);
                return List.of();
            }

            // Map results to BusinessCouponSalesResponse objects
            List<BusinessCouponSalesResponse> responses = new ArrayList<>();
            for (Object[] result : results) {
                BusinessCouponSalesResponse response = new BusinessCouponSalesResponse(
                        ((Number) result[0]).intValue(), // saleCouponId
                        ((Number) result[1]).intValue(), // businessId
                        ((Number) result[2]).intValue(), // soldCount
                        (Date) result[3]                 // buyDate
                );
                responses.add(response);
            }
            return responses;
//            return results.stream()
//                    .map(result -> new BusinessCouponSalesResponse(
//                            ((Number) result[0]).intValue(), // saleCouponId
//                            ((Number) result[1]).intValue(), // businessId
//                            ((Number) result[2]).intValue(), // soldCount
//                            (Date) result[3]                 // buyDate
//                    ))
//                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error with full stack trace for better debugging
            System.err.println("Error fetching sold coupons for business ID " + businessId);
            e.printStackTrace();
        }

        // Return an empty list in case of error
        return List.of();
    }

    @Override
    public void increaseViewCount(Integer couponId) {
        try{
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("Business not found"));
            coupon.setViewCount(coupon.getViewCount() + 1);
            coupon.setUpdatedAt(LocalDateTime.now());
            couponRepository.save(coupon);
        }catch (Exception e){
            System.out.println("E: "+ e.getMessage());
        }
    }

    //Report
    @Override
    public byte[] saleCouponReportForWeekly(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }
        // Fetch data from repository
        List<Object[]> saleData = couponRepository.saleCouponReport(businessId);
        if (saleData == null || saleData.isEmpty()) {
            throw new RuntimeException("No sales data available for the given business ID");
        }
        // Get the current date and date 7 days ago
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgo = calendar.getTime();
        // Convert Object[] data into CouponSalesBusinessResponse list, filtering for the last 7 days
        List<CouponSalesBusinessResponse> salesData = saleData.stream()
                .map(row -> {
                    Date buyDate = (Date) row[2];

                    // Filter out records older than 7 days
                    if (buyDate.before(sevenDaysAgo) || buyDate.after(currentDate)) {
                        return null; // Exclude data outside the last 7 days
                    }

                    CouponSalesBusinessResponse response = new CouponSalesBusinessResponse(
                            (Integer) row[0],  // businessId
                            (String) row[1],   // businessName
                            buyDate,           // buyDate
                            ((Number) row[3]).longValue(),  // soldQuantity
                            ((Number) row[4]).doubleValue() // totalPrice
                    );

                    System.out.println("Transformed record: " + response);

                    return response;
                })
                .filter(Objects::nonNull) // Remove any null entries (records outside the last 7 days)
                .collect(Collectors.toList());

        // Debug: Print the transformed data
        salesData.forEach(record -> {
            System.out.println("Record: " + record.getBuyDate() + ", " + record.getSoldQuantity() + ", " + record.getTotalPrice());
        });

        // Prepare the data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(salesData);
        System.out.println("Data source size: " + dataSource.getData().size()); // Check size

        // Prepare the parameters for the report
        String businessName = salesData.isEmpty() ? "" : salesData.get(0).getBusinessName();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessName", businessName); // Ensure the parameter name matches the JRXML

        // Fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/sale-coupon-b.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Return the report in the requested format
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }
    @Override
    public List<CouponUsedResponse> getAllCouponUsages(Integer shopId) {
        // Fetching the raw data from the repository
        List<Object[]> results = couponValidationRepository.findAllCouponUsages(shopId);

        // Creating a list to hold the CouponUsageDTO objects
        List<CouponUsedResponse> usages = new ArrayList<>();

        // Iterating over the results and converting them into CouponUsageDTO
        for (Object[] row : results) {
            String userName = (String) row[0];
            String email = (String) row[1];
            LocalDateTime usedAt = (LocalDateTime) row[2];
            String productName = (String) row[3];

            // Creating a new CouponUsageDTO object and adding it to the list
            CouponUsedResponse couponUsageDTO = new CouponUsedResponse(userName, email, usedAt, productName);
            usages.add(couponUsageDTO);
        }

        return usages;
    }
    @Override
    public byte[] saleCouponReportForMonthly(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch data from repository
        List<Object[]> saleData = couponRepository.saleCouponReport(businessId);

        if (saleData == null || saleData.isEmpty()) {
            throw new RuntimeException("No sales data available for the given business ID");
        }

        // Get the current date and date 7 days ago
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        Date sevenDaysAgo = calendar.getTime();

        // Convert Object[] data into CouponSalesBusinessResponse list, filtering for the last 7 days
        List<CouponSalesBusinessResponse> salesData = saleData.stream()
                .map(row -> {
                    Date buyDate = (Date) row[2];

                    // Filter out records older than 7 days
                    if (buyDate.before(sevenDaysAgo) || buyDate.after(currentDate)) {
                        return null; // Exclude data outside the last 7 days
                    }

                    CouponSalesBusinessResponse response = new CouponSalesBusinessResponse(
                            (Integer) row[0],  // businessId
                            (String) row[1],   // businessName
                            buyDate,           // buyDate
                            ((Number) row[3]).longValue(),  // soldQuantity
                            ((Number) row[4]).doubleValue() // totalPrice
                    );

                    System.out.println("Transformed record: " + response);

                    return response;
                })
                .filter(Objects::nonNull) // Remove any null entries (records outside the last 7 days)
                .collect(Collectors.toList());

        // Debug: Print the transformed data
        salesData.forEach(record -> {
            System.out.println("Record: " + record.getBuyDate() + ", " + record.getSoldQuantity() + ", " + record.getTotalPrice());
        });

        // Prepare the data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(salesData);
        System.out.println("Data source size: " + dataSource.getData().size()); // Check size

        // Prepare the parameters for the report
        String businessName = salesData.isEmpty() ? "" : salesData.get(0).getBusinessName();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessName", businessName); // Ensure the parameter name matches the JRXML

        // Fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/sale-coupon-b.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Return the report in the requested format
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    private byte[] exportToExcel(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(true);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        configuration.setAutoFitPageHeight(true);
        configuration.setColumnWidthRatio(1.5f);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return baos.toByteArray();
    }

    private CouponResponse mapToResponseDTO(CouponEntity coupon) {
        CouponResponse responseDTO = modelMapper.map(coupon, CouponResponse.class);
        responseDTO.setQuantity(coupon.getCouponRemain());
        responseDTO.setCouponCode(coupon.getProduct().getName());

        return responseDTO;
    }
}
