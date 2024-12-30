package com.css.coupon_sale.controller;

import com.css.coupon_sale.dto.request.ProductRequest;
import com.css.coupon_sale.dto.request.ProductUpdateRequest;
import com.css.coupon_sale.dto.response.BusinessResponse;
import com.css.coupon_sale.dto.response.ProductResponse;
import com.css.coupon_sale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Value("${product.image.upload-dir}") // Specify folder path in application.properties
    private String uploadDir;

    @Autowired
    private ProductService service;

    @PostMapping                 //create
    public ResponseEntity<ProductResponse> saveProduct(@ModelAttribute ProductRequest request) throws IOException {
        ProductResponse response = service.saveProduct(request);
        if (response != null ){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping                 //list
    public ResponseEntity<List<ProductResponse>> showAllCourses(){
        List<ProductResponse> response =service.showAllProducts();
        return  ResponseEntity.ok(response);
    }





    @GetMapping("/b/{id}")
    public ResponseEntity<List<ProductResponse>> getByBusiness(@PathVariable("id")Integer id){
         List<ProductResponse> responses = service.showByBusinessId(id);
         if(responses != null){
             return ResponseEntity.ok(responses);
         }
         return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ProductResponse> showbyId(@PathVariable("id")Integer id){
        ProductResponse response =service.showProductbyId(id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updatebyId(@PathVariable("id")Integer id, @ModelAttribute  ProductUpdateRequest request) throws IOException{
        ProductResponse response =service.updatebyId(id, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/discount")
    public ResponseEntity<ProductResponse> updateProductDiscount(@PathVariable("id") Integer id, @RequestBody ProductUpdateRequest discountRequest) {
        ProductResponse response = service.updateProductDiscount(id, discountRequest.getDiscount());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public void deletebyId(@PathVariable("id")Integer id){
        service.deletebyId(id);
    }

    @PostMapping("/findbyname")
    public ResponseEntity<ProductResponse> findByName(@RequestParam String name){
        ProductResponse response=service.findProductName(name);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/findbycategory")
    public ResponseEntity<ProductResponse>findByCategory(@RequestParam String category){
        ProductResponse response=service.findProductCategroy(category);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importProducts(@RequestParam("file") MultipartFile file) {
        try {
            service.importProductsFromExcel(file);
            return ResponseEntity.ok("Products imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing products: " + e.getMessage());
        }
    }
}