package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.request.ProductUpdateRequest;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.ProductService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.util.*;

import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.ProductEntity;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository repo;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;


    @Override
    public ProductResponse saveProduct(ProductRequest productRequest) throws IOException {
        BusinessEntity business  =new BusinessEntity();
        business.setId(productRequest.getBusinessId());
        ProductEntity product = new ProductEntity();
        product.setBusiness(business);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setStatus(true);

        // Save the uploaded image
        MultipartFile imageFile = productRequest.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("IMage is exist");
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir+"/product", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());
            product.setImagePath(fileName); // Save file name/path
        }
        ProductEntity productEntity = repo.save(product);
        ProductResponse response = new ProductResponse();
        response.setId(productEntity.getId());
        response.setBusinessName(productEntity.getBusiness().getName());
        response.setName(productEntity.getName());
        response.setDescription(productEntity.getDescription());
        response.setPrice(productEntity.getPrice());
        response.setStatus(productEntity.isStatus());
        response.setDiscount(productEntity.getDiscount());
        response.setCreatedAt(productEntity.getCreatedAt());


        response.setImagePath(productEntity.getImagePath());


        return response;
    }

    @Override
    public List<ProductResponse> showByBusinessId(Integer id) {
        List<ProductEntity> productEntities = repo.findByBusiness_Id(id);
        return productEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductResponse> showAllProducts() {
        List<ProductEntity> products=repo.findAll();

        System.out.println("Products" + products);
        List<ProductResponse> response = products.stream()
                .map(product->mapper.map(product, ProductResponse.class)).toList();

        return response;
    }
    @Override
    public ProductResponse showProductbyId(Integer id) {
        Optional<ProductEntity> product= repo.findById(id);
        //.orElseThrow(()-> new CourseNotFoundException("Course not found"));

        ProductResponse response=mapper.map(product, ProductResponse.class);
        return response;
    }


    @Override
    public ProductResponse updatebyId(Integer id, ProductUpdateRequest request) throws IOException {
        ProductEntity productEntity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update basic product details
        productEntity.setName(request.getName());
        productEntity.setDiscount(request.getDiscount());
        productEntity.setPrice(request.getPrice());
        productEntity.setDescription(request.getDescription());
        productEntity.setUpdatedAt(LocalDateTime.now());

        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Image is being updated");
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + "/product", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());

            // Delete old image if exists
            if (productEntity.getImagePath() != null) {
                Path oldFilePath = Paths.get(uploadDir + "/product", productEntity.getImagePath());
                try { Files.deleteIfExists(oldFilePath); } catch (DirectoryNotEmptyException e) {
                    System.err.println("Directory not empty: " + e.getMessage());
                    Files.walk(oldFilePath) .sorted(Comparator.reverseOrder()) .map(Path::toFile) .forEach(File::delete);
                    Files.deleteIfExists(oldFilePath);
                }
            }
            productEntity.setImagePath(fileName);
        }


        // Save the updated entity
        ProductEntity updatedProductEntity = repo.save(productEntity);

        // Map updated entity to response DTO
        ProductResponse response = new ProductResponse();
        response.setId(updatedProductEntity.getId());
        response.setBusinessName(updatedProductEntity.getBusiness().getName());
        response.setName(updatedProductEntity.getName());
        response.setDescription(updatedProductEntity.getDescription());
        response.setPrice(updatedProductEntity.getPrice());
        response.setStatus(updatedProductEntity.isStatus());
        response.setDiscount(updatedProductEntity.getDiscount());

        response.setImagePath(updatedProductEntity.getImagePath());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.setUpdatedAt(updatedProductEntity.getUpdatedAt().format(formatter));
        return response;
    }
    @Override
    public ProductResponse updateProductDiscount(Integer id, Float discount) {
        ProductEntity productEntity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update only the discount field
        productEntity.setDiscount(discount);
        productEntity.setUpdatedAt(LocalDateTime.now());

        // Save the updated entity
        ProductEntity updatedProductEntity = repo.save(productEntity);

        // Map updated entity to response DTO
        return mapToProductResponse(updatedProductEntity);
    }

    private ProductResponse mapToProductResponse(ProductEntity entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setBusinessName(entity.getBusiness().getName());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPrice(entity.getPrice());
        response.setStatus(entity.isStatus());
        response.setDiscount(entity.getDiscount());
        response.setImagePath(entity.getImagePath());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.setUpdatedAt(entity.getUpdatedAt().format(formatter));

        return response;
    }


    @Override
    public void deletebyId(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public ProductResponse findProductName(String name) {
        ProductEntity product= repo.findProductName(name);
        ProductRequest request=mapper.map(product, ProductRequest.class);
        return mapper.map(request,ProductResponse.class);
    }

    @Override
    public ProductResponse findProductCategroy(String category) {
        ProductEntity product= repo.findProductName(category);
        ProductRequest request=mapper.map(product, ProductRequest.class);
        return mapper.map(request,ProductResponse.class);

    }

    @Override
    public void importProductsFromExcel(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("The uploaded file is empty.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                ProductEntity product = new ProductEntity();
                BusinessEntity business = new BusinessEntity();

                // Assuming column 0: Business ID, 1: Name, 2: Description, 3: Price, 4: Discount
                business.setId((int) row.getCell(0).getNumericCellValue());
                product.setBusiness(business);
                product.setName(row.getCell(1).getStringCellValue());
                product.setDescription(row.getCell(2).getStringCellValue());
                product.setPrice(row.getCell(3).getNumericCellValue());
                product.setDiscount(0.0f);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                product.setStatus(true);

                repo.save(product);
            }

            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import products: " + e.getMessage(), e);
        }
    }

    private ProductResponse mapToResponseDTO(ProductEntity product) {
        ProductResponse responseDTO = mapper.map(product, ProductResponse.class);
        responseDTO.setBusinessName(product.getBusiness().getName());
        return responseDTO;
    }



    @Override
    public byte[] generateProductListReportForBusiness(Integer businessId, String reportType) throws JRException {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        // Fetch product data based on business ID
        List<ProductEntity> products = repo.findByBusiness_Id(businessId);

        // Filter products with status 1
        // Filter products with status true (assuming status is a boolean)
        List<ProductEntity> activeProducts = products.stream()
                .filter(product -> product.isStatus())  // This checks if the status is true
                .collect(Collectors.toList());

        // Log the filtered products for debugging
        System.out.println("Fetched active products for businessId " + businessId + " with status 1:");
        if (activeProducts != null && !activeProducts.isEmpty()) {
            for (ProductEntity product : activeProducts) {
                System.out.println("Product Name: " + product.getName() + ", Category: " + product.getCategory() +
                        ", Price: " + product.getPrice() + ", Description: " + product.getDescription());
            }
        } else {
            System.out.println("No active products found for businessId " + businessId);
        }

        // Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessId", businessId);
        parameters.put("reportDate", new java.util.Date());

        // Log the parameters for debugging
        System.out.println("Report parameters: " + parameters);

        // Load the JRXML file (make sure the path is correct)
        InputStream reportInputStream = getClass().getResourceAsStream("/product-b.jrxml");
        if (reportInputStream == null) {
            throw new IllegalArgumentException("Report template (product-b.jrxml) not found");
        }

        // Compile the JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(reportInputStream);

        // Fill the report with data from the filtered products
        JRDataSource dataSource = new JRBeanCollectionDataSource(activeProducts);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Log JasperPrint object for debugging
        System.out.println("JasperPrint filled successfully. Number of pages: " + jasperPrint.getPages().size());

        // Export the report to the requested format
        if ("pdf".equalsIgnoreCase(reportType)) {
            byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);
            System.out.println("Generated PDF report.");
            return pdfData;
        } else if ("excel".equalsIgnoreCase(reportType)) {
            byte[] excelData = exportToExcel(jasperPrint);
            System.out.println("Generated Excel report.");
            return excelData;
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


}
