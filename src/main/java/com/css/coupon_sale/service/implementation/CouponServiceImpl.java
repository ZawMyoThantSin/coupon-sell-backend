package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.CouponRequest;
import com.css.coupon_sale.dto.response.*;
import com.css.coupon_sale.entity.CouponEntity;
import com.css.coupon_sale.entity.ProductEntity;
import com.css.coupon_sale.repository.*;
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
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    private final ProductRepository productRepository;
    private final CouponValidationRepository couponValidationRepository;
    private final ModelMapper modelMapper;
    private final SaleCouponRepository saleCouponRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository, ProductRepository productRepository, ModelMapper modelMapper, CouponValidationRepository couponValidationRepository, SaleCouponRepository saleCouponRepository, OrderRepository orderRepository) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.couponValidationRepository=couponValidationRepository;
        this.saleCouponRepository = saleCouponRepository;
        this.orderRepository = orderRepository;
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
    public byte[]   saleCouponReportForWeekly(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch data from repository
        List<Object[]> saleData = couponRepository.saleCouponReport(businessId);

        if (saleData == null || saleData.isEmpty()) {
            throw new RuntimeException("No sales data available for the given business ID");
        }

        // Get the current date and date 30 days ago
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgo = calendar.getTime();

        // Convert Object[] data into CouponSalesBusinessResponse list, filtering for the last 30 days
        List<CouponSalesBusinessResponse> salesData = saleData.stream()
                .map(row -> {
                    // Perform type checks before casting
                    if (row[0] instanceof Integer && row[1] instanceof String && row[2] instanceof Date &&
                            row[3] instanceof Integer && row[4] instanceof String &&
                            row[5] instanceof Number && row[6] instanceof Number) {

                        Date buyDate = (Date) row[2];

                        // Filter out records older than 30 days
                        if (buyDate.before(sevenDaysAgo) || buyDate.after(currentDate)) {
                            return null; // Exclude data outside the last 30 days
                        }

                        // Ensure proper casting and type handling
                        CouponSalesBusinessResponse response = new CouponSalesBusinessResponse(
                                (Integer) row[0],  // businessId
                                (String) row[1],   // businessName
                                (String) row[4],   // productName
                                buyDate,           // buyDate
                                ((Number) row[5]).longValue(),  // soldQuantity
                                ((Number) row[6]).doubleValue() // totalPrice
                        );

                        System.out.println("Transformed record: " + response);

                        return response;
                    } else {
                        // Log or handle the invalid row type situation
                        System.out.println("Invalid row types: " + Arrays.toString(row));
                        return null; // Skip the row if type is incorrect
                    }
                })
                .filter(Objects::nonNull) // Remove any null entries (invalid rows)
                .collect(Collectors.toList());


        // Calculate the total price for the last 30 days
        double totalPriceForWeekly = salesData.stream()
                .mapToDouble(CouponSalesBusinessResponse::getTotalPrice)
                .sum();

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
        parameters.put("businessName", businessName); // Ensure businessName is a String here
        parameters.put("totalPriceForWeekly", String.format("%.2f MMK", totalPriceForWeekly)); // Format as string for JRXML

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
    public List<CouponUsedResponse> getAllCouponUsages(Integer businessId) {
        // Fetching the raw data from the repository
        List<Object[]> results = couponValidationRepository.findAllCouponUsages(businessId);

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

        // Get the current date and date 30 days ago
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        Date thirtyDaysAgo = calendar.getTime();

        // Convert Object[] data into CouponSalesBusinessResponse list, filtering for the last 30 days
        List<CouponSalesBusinessResponse> salesData = saleData.stream()
                .map(row -> {
                    // Perform type checks before casting
                    if (row[0] instanceof Integer && row[1] instanceof String && row[2] instanceof Date &&
                            row[3] instanceof Integer && row[4] instanceof String &&
                            row[5] instanceof Number && row[6] instanceof Number) {

                        Date buyDate = (Date) row[2];

                        // Filter out records older than 30 days
                        if (buyDate.before(thirtyDaysAgo) || buyDate.after(currentDate)) {
                            return null; // Exclude data outside the last 30 days
                        }

                        // Ensure proper casting and type handling
                        CouponSalesBusinessResponse response = new CouponSalesBusinessResponse(
                                (Integer) row[0],  // businessId
                                (String) row[1],   // businessName
                                (String) row[4],   // productName
                                buyDate,           // buyDate
                                ((Number) row[5]).longValue(),  // soldQuantity
                                ((Number) row[6]).doubleValue() // totalPrice
                        );

                        System.out.println("Transformed record: " + response);

                        return response;
                    } else {
                        // Log or handle the invalid row type situation
                        System.out.println("Invalid row types: " + Arrays.toString(row));
                        return null; // Skip the row if type is incorrect
                    }
                })
                .filter(Objects::nonNull) // Remove any null entries (invalid rows)
                .collect(Collectors.toList());


        // Calculate the total price for the last 30 days
        double totalPriceForMonthly = salesData.stream()
                .mapToDouble(CouponSalesBusinessResponse::getTotalPrice)
                .sum();

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
        parameters.put("businessName", businessName); // Ensure businessName is a String here
        parameters.put("totalPriceForMonthly", String.format("%.2f MMK", totalPriceForMonthly)); // Format as string for JRXML

        // Fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/sale-coupon-b-m.jrxml"));
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
        responseDTO.setOriginalQuantity(coupon.getQuantity());
        responseDTO.setQuantity(coupon.getCouponRemain());
        responseDTO.setCouponCode(coupon.getProduct().getName());

        return responseDTO;
    }



    @Override
    public byte[] generateCouponUsageReportweekly(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }
        // Fetch data from the repository
        List<Object[]> couponUsageData = couponValidationRepository.findAllCouponUsages(businessId);

        if (couponUsageData == null || couponUsageData.isEmpty()) {
            throw new RuntimeException("No coupon usage data available for the given shop ID");
        }
        // Get the current date and adjust the date range based on the period
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -7);  // Last 7 days
        Date sevenDaysAgo = calendar.getTime();

        // Transform data to DTO, filtering for the specified period
        List<CouponUsageResoponse> usageData = couponUsageData.stream()
                .map(row -> {
                    // Safely access the elements
                    String userName = (String) row[0];  // userName
                    String email = (String) row[1];     // email
                    Object usedAtObj = row[2];          // usedAt
                    String productName = (String) row[3]; // productName
                    String businessName = (String)row[4]; //businessName
                    // Handle the usedAt value conversion
                    Date usedAt = null;
                    if (usedAtObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) usedAtObj;
                        usedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (usedAtObj instanceof Date) {
                        usedAt = (Date) usedAtObj;
                    }
                    // Filter out rows not in the last 7 days
                    if (usedAt == null || usedAt.before(sevenDaysAgo)|| usedAt.after(currentDate))  {
                        return null;
                    }
                    // Create DTO object from the row data
                    CouponUsageResoponse response = new CouponUsageResoponse(userName, email, usedAt, productName,businessName);
                    return response;
                })
                .filter(Objects::nonNull) // Remove null entries (excluded records)
                .collect(Collectors.toList());
        usageData.forEach(record ->{
        });
        // Prepare data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usageData);

        // Prepare report parameters
        String businessName = usageData.isEmpty() ? "" : usageData.get(0).getBusinessName();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("shopName", businessName);  // Replace with actual shop name if needed

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/usedCoupon.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report based on the type
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }







    @Override
    public byte[] generateCouponUsageReportmonthly(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }
        // Fetch data from the repository
        List<Object[]> couponUsageData = couponValidationRepository.findAllCouponUsages(businessId);

        if (couponUsageData == null || couponUsageData.isEmpty()) {
            throw new RuntimeException("No coupon usage data available for the given shop ID");
        }
        // Get the current date and adjust the date range based on the period
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);  // Last 7 days
        Date sevenDaysAgo = calendar.getTime();

        // Transform data to DTO, filtering for the specified period
        List<CouponUsageResoponse> usageData = couponUsageData.stream()
                .map(row -> {
                    // Safely access the elements
                    String userName = (String) row[0];  // userName
                    String email = (String) row[1];     // email
                    Object usedAtObj = row[2];          // usedAt
                    String productName = (String) row[3]; // productName
                    String businessName = (String)row[4]; //businessName

                    // Handle the usedAt value conversion
                    Date usedAt = null;
                    if (usedAtObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) usedAtObj;
                        usedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (usedAtObj instanceof Date) {
                        usedAt = (Date) usedAtObj;
                    }
                    // Filter out rows not in the last 7 days
                    if (usedAt == null || usedAt.before(sevenDaysAgo)|| usedAt.after(currentDate))  {
                        return null;
                    }
                    // Create DTO object from the row data
                    CouponUsageResoponse response = new CouponUsageResoponse(userName, email, usedAt, productName,businessName);
                    return response;
                })
                .filter(Objects::nonNull) // Remove null entries (excluded records)
                .collect(Collectors.toList());
        usageData.forEach(record ->{
        });
        // Prepare data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usageData);

        // Prepare report parameters
        String businessName = usageData.isEmpty() ? "" : usageData.get(0).getBusinessName();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("shopName", businessName);  // Replace with actual shop name if needed

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/usedCoupon-m.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report based on the type
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    @Override
    public byte[] generateCouponReport(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch coupon report data for the given businessId from the repository
        List<Object[]> couponReportData = couponRepository.getCouponReport(businessId);

        if (couponReportData == null || couponReportData.isEmpty()) {
            throw new RuntimeException("No coupon data available for the given business ID");
        }

        // Transform the fetched data into a response DTO suitable for report generation
        List<CouponReportResponse> reportData = couponReportData.stream()
                .map(row -> {
                    String productName = (String) row[0];          // productName
                    Double productDiscount = ((Number) row[1]).doubleValue(); // productDiscount
                    Integer quantity = (Integer) row[2];           // quantity
                    java.util.Date expiredDate = row[3] instanceof java.time.LocalDateTime
                            ? java.util.Date.from(((java.time.LocalDateTime) row[3]).atZone(java.time.ZoneId.systemDefault()).toInstant())
                            : (java.util.Date) row[3];
                    Double price = ((Number) row[4]).doubleValue(); // price
                    String businessName = (String) row[5];         // businessName fetched from product's business

                    return new CouponReportResponse(productName, productDiscount, quantity, expiredDate, price, businessName);
                })
                .collect(Collectors.toList());


        // Prepare data source for Jasper report
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        // Prepare report parameters (no need to fetch business name separately)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessName", reportData.isEmpty() ? "" : reportData.get(0).getBusinessName());  // Add business name as parameter

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/coupon.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report in the requested format (PDF or Excel)
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    @Override
    public List<CouponUsed2Response> getAllCoupon(Integer businessId) {
        // Fetching the raw data from the repository
        List<Object[]> results = couponValidationRepository.findAllCouponUsages(businessId);

        // Creating a list to hold the CouponUsedResponse objects
        List<CouponUsed2Response> usages = new ArrayList<>();

        // Iterating over the results and converting them into CouponUsedResponse
        for (Object[] row : results) {
            String userName = (String) row[0];
            String email = (String) row[1];
            LocalDateTime usedAtLocalDateTime = (LocalDateTime) row[2]; // Assuming usedAt is LocalDateTime in the database
            String productName = (String) row[3];

            // Convert LocalDateTime to Date
            Date usedAt = Date.from(usedAtLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

            // Create a new CouponUsedResponse object and add it to the list
            CouponUsed2Response couponUsageDTO = new CouponUsed2Response(userName, email, usedAt, productName);
            usages.add(couponUsageDTO);
        }

        return usages;
    }

    @Override
    public byte[] generateCouponUsageReport(List<CouponUsed2Response> couponUsages, String reportType) throws JRException {
        // Load the JasperReport template
        InputStream reportStream = getClass().getResourceAsStream("/coupon_usage_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Convert the list of coupon usages to a JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(couponUsages);

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        // Export the report to the desired format
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
            return outputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Invalid report type");
        }
    }

    @Override
    public List<CouponSalesBusinessResponse2> getAllCouponSales(Integer businessId) {
        // Fetching the raw data from the repository
        List<Object[]> results = saleCouponRepository.findAllSaleCoupon(businessId);

        // Creating a list to hold the CouponSalesBusinessResponse objects
        List<CouponSalesBusinessResponse2> sales = new ArrayList<>();

        // Iterating over the results and converting them into CouponSalesBusinessResponse
        for (Object[] row : results) {
            Integer businessIdResult = (Integer) row[0];
            String businessName = (String) row[1];
            LocalDateTime buyDateLocalDateTime = (LocalDateTime) row[2]; // Assuming buyDate is LocalDateTime in the database
            Long soldQuantity = (Long) row[3];
            Double totalPrice = (Double) row[4];
            String productName = (String) row[5];

            // Convert LocalDateTime to Date
            Date buyDate = Date.from(buyDateLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

            // Create a new CouponSalesBusinessResponse object and add it to the list
            CouponSalesBusinessResponse2 couponSale = new CouponSalesBusinessResponse2(businessIdResult, businessName, buyDate, soldQuantity, totalPrice, productName);
            sales.add(couponSale);
        }

        // Debugging: Log product names to ensure they're populated correctly
        for (CouponSalesBusinessResponse2 response : sales) {
            System.out.println("Product Name: " + response.getProductName());
        }

        return sales;
    }

    @Override
    public byte[] generateCouponSalesReport(List<CouponSalesBusinessResponse2> couponSales, String reportType) throws JRException {
        // Load the JasperReport template
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("sale-coupon-all.jrxml");
        if (reportStream == null) {
            throw new RuntimeException("Template file 'sale_coupon_b.jrxml' not found in the resources directory.");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Convert the list of coupon sales to a JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(couponSales);

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

        // Export the report to the desired format
        if ("pdf".equalsIgnoreCase(reportType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
            return outputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Invalid report type");
        }

    }

    @Override
    public byte[] generateRemainingCouponReport(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch data from the repository as Object[]
        List<Object[]> remainingCouponsData = saleCouponRepository.findRemainingCoupons(businessId);

        if (remainingCouponsData == null || remainingCouponsData.isEmpty()) {
            throw new RuntimeException("No remaining coupons found for the given business ID");
        }

        // Filter data and convert LocalDateTime to Date if necessary
        List<RemainingCouponResponse> filteredCoupons = remainingCouponsData.stream()
                .map(data -> {
                    String email = (String) data[0];
                    Object buyDateObj = data[1]; // Could be LocalDateTime or Date
                    Object expiredDateObj = data[2]; // Could be LocalDateTime or Date
                    Double totalPrice = (Double) data[3];
                    String productName = (String) data[4];

                    // Convert LocalDateTime to Date
                    Date buyDate = null;
                    if (buyDateObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) buyDateObj;
                        buyDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (buyDateObj instanceof Date) {
                        buyDate = (Date) buyDateObj;
                    }

                    Date expiredDate = null;
                    if (expiredDateObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) expiredDateObj;
                        expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (expiredDateObj instanceof Date) {
                        expiredDate = (Date) expiredDateObj;
                    }

                    return new RemainingCouponResponse(email, buyDate, expiredDate, totalPrice, productName);
                })
                .collect(Collectors.toList());

        // Calculate total count
        int totalCount = filteredCoupons.size();

        // Prepare data source for JasperReport
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(filteredCoupons);

        // Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessId", businessId); // Replace with actual business details if needed
        parameters.put("totalCount", totalCount); // Add totalCount as a parameter

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/RemainingCoupon.jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report based on the type
        byte[] reportBytes = null;
        if ("pdf".equalsIgnoreCase(reportType)) {
            reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            reportBytes = exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }

        return reportBytes;
    }

    @Override
    public byte[] generateExpiredCouponDataReport(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch expired coupon data from repository
        List<Object[]> expiredCouponsData = couponRepository.findExpiredCouponsByBusinessId(businessId);

        if (expiredCouponsData == null || expiredCouponsData.isEmpty()) {
            throw new RuntimeException("No expired products found for the given business ID");
        }

        // Map expired coupons data to ExpiredCouponDataResponse DTO
        List<ExpiredCouponDataResponse> expiredCoupons = expiredCouponsData.stream()
                .map(data -> {
                    // Extract fields from the query result
                    Object expiredDateObj = data[0];
                    String productName = (String) data[1];
                    Double price = (data[2] instanceof Float) ? ((Float) data[2]).doubleValue() : (Double) data[2];
                    Integer totalQuantity = (data[3] instanceof Long) ? ((Long) data[3]).intValue() : (Integer) data[3];
                    Integer soldOutQuantity = (data[4] instanceof Long) ? ((Long) data[4]).intValue() : (Integer) data[4];

                    // Convert expired date
                    Date expiredDate = null;
                    if (expiredDateObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) expiredDateObj;
                        expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (expiredDateObj instanceof Date) {
                        expiredDate = (Date) expiredDateObj;
                    }

                    // Create and return the DTO object
                    return new ExpiredCouponDataResponse(expiredDate, productName, price, totalQuantity, soldOutQuantity);
                })
                .collect(Collectors.toList());

        // Create a JRBeanCollectionDataSource with the expiredCoupons list (not expiredCouponsData)
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(expiredCoupons);

        // Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessId", businessId);

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/expiredCouponsData.jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report based on the type
        byte[] reportBytes;
        if ("pdf".equalsIgnoreCase(reportType)) {
            reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            reportBytes = exportToExcel(jasperPrint); // Ensure this method exists and is implemented correctly
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }

        return reportBytes;
    }

    public byte[] generateBestSellingProductReport(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch data from the repository
        List<Object[]> bestSellingProductsData = orderRepository.findBestSellingProducts(businessId);

        if (bestSellingProductsData == null || bestSellingProductsData.isEmpty()) {
            throw new RuntimeException("No best-selling products found for the given business ID");
        }

        List<BestSellingProductResponse> bestSellingProducts = bestSellingProductsData.stream()
                .map(data -> {
                    String productName = (String) data[0];
                    // Ensure price and totalPrice are handled as Double, even if they are Float
                    Double price = (data[1] instanceof Float) ? ((Float) data[1]).doubleValue() : (Double) data[1];
                    Double discount = (data[2] instanceof Float) ? ((Float) data[2]).doubleValue() : (Double) data[2];
                    Double totalPrice = (data[3] instanceof Float) ? ((Float) data[3]).doubleValue() : (Double) data[3];

                    // Convert Long to Integer (if data[4] is Long)
                    Integer quantity = (data[4] instanceof Long) ? ((Long) data[4]).intValue() : (Integer) data[4];

                    return new BestSellingProductResponse(productName, price,discount, totalPrice, quantity);
                })
                .collect(Collectors.toList());




        // Create a JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bestSellingProducts);

        // Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessId", businessId);

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/BestSellingProducts.jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report based on the type
        byte[] reportBytes;
        if ("pdf".equalsIgnoreCase(reportType)) {
            reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportType)) {
            reportBytes = exportToExcel(jasperPrint); // Ensure this method exists and is implemented correctly
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }

        return reportBytes;
    }

}
