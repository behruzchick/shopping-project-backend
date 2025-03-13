package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.enurmation.ProductType;
import com.example.demo.model.enurmation.Status;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product/create")
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") String status,
            @RequestParam("type") String type,
            @RequestParam MultipartFile file
    ) throws IOException {
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".webp") || fileName.endsWith(".jpeg")) {
            file.transferTo(new File(filePath));
            Product product = new Product();

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStatus(Status.valueOf(status));
            product.setProductType(ProductType.valueOf(type));
            product.setImageUrl("/images/" + fileName);

            return ResponseEntity.ok().body(productService.createProduct(product));
        }else {
            return ResponseEntity.badRequest().body("Unsupported Media Type");
        }

    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try{
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Дефолтный тип, если не удалось определить
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("type") String type,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") String status,
            @RequestParam("file") MultipartFile file

    ) throws IOException {
        File uploadDirFile = new File(uploadDir);
        Product current_product = productService.getProduct(id);

        if (current_product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".webp") || fileName.endsWith(".jpeg")) {
            file.transferTo(new File(filePath));

            current_product.setName(name);
            current_product.setDescription(description);
            current_product.setPrice(price);
            current_product.setStatus(Status.valueOf(status));
            current_product.setProductType(ProductType.valueOf(type));
            current_product.setImageUrl("/images/" + fileName);

            return ResponseEntity.ok().body(productService.updateProduct(current_product));
        }else {
            return ResponseEntity.badRequest().body("Unsupported Media Type");
        }
    }

    @GetMapping("/product/get/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/product/all")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/product/topPrice")
    public ResponseEntity<?> getProductTopPrice(Pageable pageable) {
        Page<Product> products = productService.getAllProductByHighPrice(pageable);
        if (products.getTotalElements() > 0) {
            return ResponseEntity.ok().body(products);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
    }

    @GetMapping("/product/lowPrice")
    public ResponseEntity<?> getProductLowPrice(Pageable pageable) {
        Page<Product> products = productService.getAllProductByLowPrice(pageable);
        if (products.getTotalElements() > 0) {
            return ResponseEntity.ok().body(products);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
    }

    @PutMapping("/product/voteRate/{id}")
    public ResponseEntity<?> updateProductVoteRate(@RequestBody Map<String, Double> rating, @PathVariable Long id) {
        Product current_product = productService.getProduct(id);
        if (rating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rating cannot be null");
        }
        Double rate = rating.get("rating");
        if (current_product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        Product rated_product = productService.updateRating(current_product , rate);
        return ResponseEntity.ok().body(rated_product);
    }

    @GetMapping("/product/topRated")
    public ResponseEntity<?> getProductTopRated(Pageable pageable) {
        Page<Product> products = productService.getAllProductByTopRated(pageable);
        if (products.getTotalElements() > 0) {
            return ResponseEntity.ok().body(products);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
    }

    @GetMapping("/product/filterByName")
    public ResponseEntity<?> getProductFilterByName(Pageable pageable , @RequestParam String name) {
        Page<Product> products = productService.getAllProductByName(pageable, name);
        if (products.getTotalElements() > 0) {
            return ResponseEntity.ok().body(products);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body("Product deleted successfully");
    }

}
