package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.request.ProductUpdateRequest;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.ProductService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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

import java.util.Optional;
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

    private ProductResponse mapToResponseDTO(ProductEntity product) {
        ProductResponse responseDTO = mapper.map(product, ProductResponse.class);
        responseDTO.setBusinessName(product.getBusiness().getName());
        return responseDTO;
    }


}
