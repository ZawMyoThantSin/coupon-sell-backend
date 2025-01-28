package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.BusinessRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.request.UpdateBusinessRequest;
import com.css.coupon_sale.dto.response.*;
import com.css.coupon_sale.entity.BusinessCategoryEntity;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.PaidHistoryEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.*;
import com.css.coupon_sale.service.BusinessReviewService;
import com.css.coupon_sale.service.BusinessService;
import com.css.coupon_sale.service.PaidHistoryService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class BusinessServiceImpl implements BusinessService  {

    private final BusinessRepository businessRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BusinessCategoryRepository categoryRepository;

    private final BusinessReviewService businessReviewService;

    private final SaleCouponRepository saleCouponRepository;

    private final PaidHistoryService paidHistoryService;

    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, ModelMapper modelMapper,
                               BusinessCategoryRepository categoryRepository, BusinessReviewService businessReviewService,
                               SaleCouponRepository saleCouponRepository, PaidHistoryService paidHistoryService) {
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.businessReviewService = businessReviewService;
        this.saleCouponRepository = saleCouponRepository;
        this.paidHistoryService = paidHistoryService;
    }

    @Override
    public SignupResponse addBusinessOwner(SignupRequest request) {
        Optional<UserEntity> oUser = userRepository.findByEmail(request.getEmail());
        if (oUser.isPresent()){
            System.out.println("Login already exists");
            return null;
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setPassword(passwordEncoder.encode(CharBuffer.wrap(request.getPassword())));
        userEntity.setStatus(1);
        userEntity.setAuthProvider("LOCAL");
        if(userEntity.getRole() == null){
            userEntity.setRole("OWNER");
            userEntity.setAuthProvider("LOCAL");
        }
        userEntity.setCreated_at(now());
        UserEntity user = userRepository.save(userEntity);

        return modelMapper.map(user, SignupResponse.class);

    }

    @Override
    public BusinessResponse createBusiness(BusinessRequest dto) throws IOException {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessCategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        BusinessEntity business = new BusinessEntity();
        MultipartFile imageFile = dto.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Image is exist");
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir+"/business", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());
            business.setPhoto(fileName); // Save file name/path
        }

        business.setName(dto.getName());
        business.setLocation(dto.getLocation());
        business.setDescription(dto.getDescription());
        business.setContactNumber(dto.getContactNumber());
        business.setCategory(category);
        business.setUser(user);
        business.setStatus(true);
        business.setCreatedAt(LocalDateTime.now());
        business.setLastPaidAmount(0.0);
        business.setPaymentStatus("PENDING");
        business.setIncomeIncreased(false);
        System.out.println("Before Save: "+ business);
        BusinessEntity savedBusiness = businessRepository.save(business);
        Double totalIncome = getTotalIncomeForBusiness(savedBusiness.getId());
        savedBusiness.setTotalIncome(totalIncome);
        return mapToResponseDTO(savedBusiness);
    }

    @Override
    public BusinessResponse getBusinessById(Integer id) {
        BusinessEntity business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponseDTO(business);
    }

    @Override
    public BusinessResponse getByUserId(Long id) {
        BusinessEntity businessEntity = businessRepository.findByUser_Id(id);
        return mapToResponseDTO(businessEntity);
    }

    @Override
    public List<BusinessResponse> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BusinessResponse updateBusiness(Integer id, UpdateBusinessRequest requestDTO) throws IOException{
        BusinessEntity business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        UserEntity user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessCategoryEntity category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        business.setUser(user);
        business.setName(requestDTO.getName());
        business.setLocation(requestDTO.getLocation());
        business.setDescription(requestDTO.getDescription());
        business.setContactNumber(requestDTO.getContactNumber());
        business.setCategory(category);
        // Handle image upload
        MultipartFile imageFile = requestDTO.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + "/business", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());

            // Delete old image if exists
            if (business.getPhoto() != null) {
                Path oldFilePath = Paths.get(uploadDir + "/business", business.getPhoto());
                try { Files.deleteIfExists(oldFilePath); } catch (DirectoryNotEmptyException e) {
                    System.err.println("Directory not empty: " + e.getMessage());
                    Files.walk(oldFilePath) .sorted(Comparator.reverseOrder()) .map(Path::toFile) .forEach(File::delete);
                    Files.deleteIfExists(oldFilePath);
                }
            }

            business.setPhoto(fileName); // Save new file name as photo
        }

        BusinessEntity updatedBusiness = businessRepository.save(business);
        return mapToResponseDTO(updatedBusiness);
    }

    @Override
    public BusinessResponse softDeleteBusiness(Integer id) {
        BusinessEntity business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        business.setStatus(false); // Mark as inactive

        return mapToResponseDTO(businessRepository.save(business));
    }

    @Override
    public Double getTotalIncomeForBusiness(int businessId) {
        Double totalIncome = saleCouponRepository.getTotalIncomeByBusinessId(businessId);
        totalIncome = totalIncome != null ? totalIncome : 0.0;
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        business.setTotalIncome(totalIncome);
        checkIncomeIncreased(business);
        if (business.isIncomeIncreased()) {
            business.setPaymentStatus("PENDING"); // Set to PENDING if income has increased
        } else {
            business.setPaymentStatus("PAID"); // Set to PAID if income has not increased
        }
        businessRepository.save(business);

        return totalIncome;
    }

    @Override
    public Double calculateAmountToPay(Integer businessId) {
        return businessRepository.calculateAmountToPay(businessId);
    }

    public byte[] generateBusinessReport(String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch data from the repository
        List<Object[]> businessData = businessRepository.getBusinessReport();

        if (businessData == null || businessData.isEmpty()) {
            throw new RuntimeException("No business data available.");
        }

        // Transform data into DTO
        List<BusinessReportResponse> reportData = businessData.stream()
                .map(row -> {
                    String businessName = (String) row[0];
                    String contactNumber = (String) row[1];
                    Object createdAtObj = row[2];
                    String userName = (String) row[3];
                    String email = (String) row[4];

                    Date createdAt = null;
                    if (createdAtObj instanceof LocalDateTime) {
                        LocalDateTime localDateTime = (LocalDateTime) createdAtObj;
                        createdAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (createdAtObj instanceof Date) {
                        createdAt = (Date) createdAtObj;
                    }

                    return new BusinessReportResponse(
                            businessName, userName, email,
                            contactNumber, // Keep contactNumber as String
                            createdAt
                    );
                })
                .collect(Collectors.toList());

        // Prepare data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        // Compile and fill the report
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/businesses.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

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
    public byte[] generateCustomerReport(int businessId,String reportFormat) throws JRException {
        if (reportFormat == null) {
            throw new IllegalArgumentException("Report format cannot be null");
        }

        // Fetch PaidHistoryEntities
        List<PaidHistoryEntity> paidHistoryEntities = paidHistoryService.getPaidHistory(businessId);


        // Map PaidHistoryEntity to PaidHistoryDTO
        PaidHistoryMapper mapper = new PaidHistoryMapper();
        List<PaidHistoryMapper.PaidHistoryDTO> paidHistoryDTOs = mapper.mapToDTO(paidHistoryEntities);

        List<Object[]> customerData = paidHistoryDTOs.stream()
                .map(dto -> new Object[]{
                        dto.getBusinessName(),
                        dto.getAdminProfit(),
                        dto.getPercentage(),
                        dto.getPaidAmount(),
                        dto.getPaidAt()
                })
                .collect(Collectors.toList());

        System.out.println("Converted customerData: ");

        if (customerData == null || customerData.isEmpty()) {
            throw new RuntimeException("No customer data available.");
        }

        // Transform Object[] into BusinessPaidHistoryResponse DTOs
        List<BusinessPaidHistoryResponse> reportData = customerData.stream()
                .map(row -> {
                    String businessName = (String) row[0];
                    Double adminProfit = (Double) row[1];
                    Double percentage = (Double) row[2];
                    Double paidAmount = (Double) row[3];
                    Date paidAt = (Date) row[4];

                    return new BusinessPaidHistoryResponse(
                            businessName, adminProfit, percentage, paidAmount, paidAt
                    );
                })
                .collect(Collectors.toList());

        // Prepare data source for JasperReports
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        // Compile and fill the report template
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/owner-paid.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

        // Export the report based on the requested format
        if ("pdf".equalsIgnoreCase(reportFormat)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(reportFormat)) {
            return exportToExcel(jasperPrint);
        } else {
            throw new IllegalArgumentException("Unsupported report format: " + reportFormat);
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

    private void checkIncomeIncreased(BusinessEntity business) {
        // Check if the total income is greater than the last paid amount
        if (business.getTotalIncome() > business.getLastPaidAmount()) {
            business.setIncomeIncreased(true);
        } else {
            business.setIncomeIncreased(false);
        }
    }

    private BusinessResponse mapToResponseDTO(BusinessEntity business) {
        BusinessResponse responseDTO = modelMapper.map(business, BusinessResponse.class);
        responseDTO.setCategoryId(business.getCategory().getId());
        responseDTO.setUserId(business.getUser().getId());
        responseDTO.setUserName(business.getUser().getName());
        responseDTO.setUserEmail(business.getUser().getEmail());
        responseDTO.setCategory(business.getCategory().getName());
        responseDTO.setPaymentStatus(business.getPaymentStatus());
        double count = businessReviewService.calculateOverviewCount(business.getId());
        responseDTO.setCount(count);
        return responseDTO;
    }

}
