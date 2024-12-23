package com.css.coupon_sale.service.implementation;

import com.css.coupon_sale.dto.request.BusinessRequest;
import com.css.coupon_sale.dto.request.SignupRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.SignupResponse;
import com.css.coupon_sale.entity.BusinessEntity;
import com.css.coupon_sale.entity.UserEntity;
import com.css.coupon_sale.exception.AppException;
import com.css.coupon_sale.repository.BusinessRepository;
import com.css.coupon_sale.repository.UserRepository;
import com.css.coupon_sale.service.BusinessService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, ModelMapper modelMapper) {
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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
        if(userEntity.getRole() == null){
            userEntity.setRole("OWNER");
        }
        userEntity.setCreated_at(now());
        UserEntity user = userRepository.save(userEntity);

        return modelMapper.map(user, SignupResponse.class);

    }

    @Override
    public BusinessResponse createBusiness(BusinessRequest dto) throws IOException {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
        business.setCategory(dto.getCategory());
        business.setUser(user);
        business.setStatus(true);
        business.setCreatedAt(LocalDateTime.now());
        System.out.println("Before Save: "+ business);
        BusinessEntity savedBusiness = businessRepository.save(business);
        return mapToResponseDTO(savedBusiness);

//        UserEntity user = userRepository.findById(requestDTO.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        System.out.println("REQ: "+ requestDTO.toString());
//        BusinessEntity business = new BusinessEntity();
//        business.setUser(user);
//        business.setName(requestDTO.getName());
//        business.setLocation(requestDTO.getLocation());
//        business.setDescription(requestDTO.getDescription());
//        business.setContactNumber(requestDTO.getContactNumber());
//        business.setPhoto(requestDTO.getPhoto());
//        business.setCategory(requestDTO.getCategory());
//        business.setCreatedAt(LocalDateTime.now());
//
//        BusinessEntity savedBusiness = businessRepository.save(business);
//        return mapToResponseDTO(savedBusiness);
    }

    @Override
    public BusinessResponse getBusinessById(Integer id) {
        BusinessEntity business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponseDTO(business);
    }

    @Override
    public List<BusinessResponse> getByUserId(Long id) {
        List<BusinessEntity> businessEntityList = businessRepository.findByUser_Id(id);
        return businessEntityList.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessResponse> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BusinessResponse updateBusiness(Integer id, BusinessRequest requestDTO) {
        BusinessEntity business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        UserEntity user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        business.setUser(user);
        business.setName(requestDTO.getName());
        business.setLocation(requestDTO.getLocation());
        business.setDescription(requestDTO.getDescription());
        business.setContactNumber(requestDTO.getContactNumber());
//        business.setPhoto(requestDTO.getPhoto());
        business.setCategory(requestDTO.getCategory());

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

    private BusinessResponse mapToResponseDTO(BusinessEntity business) {
        BusinessResponse responseDTO = modelMapper.map(business, BusinessResponse.class);
        responseDTO.setUserName(business.getUser().getName());
        responseDTO.setUserEmail(business.getUser().getEmail());
        return responseDTO;
    }

}
