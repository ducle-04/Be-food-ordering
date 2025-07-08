package com.example.b_food_ordering.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.b_food_ordering.Dto.ProductDTO;
import com.example.b_food_ordering.Service.ProductService;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // Danh sách các trạng thái hợp lệ (giả sử sử dụng String, thay bằng enum nếu cần)
    private static final List<String> VALID_STATUSES = Arrays.asList("AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED");
    private static final List<String> VALID_SPECIAL_CATEGORIES = Arrays.asList("FEATURED", "NEW", "BESTSELLER");

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        // Kiểm tra đầu vào
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
            productDTO.getCategory() == null || productDTO.getCategory().trim().isEmpty() ||
            productDTO.getStatus() == null || productDTO.getStatus().trim().isEmpty() ||
            productDTO.getOriginalPrice() <= 0) {
            return ResponseEntity.badRequest().body("Tên sản phẩm, danh mục, trạng thái và giá gốc không được để trống hoặc không hợp lệ");
        }

        // Kiểm tra trạng thái hợp lệ
        if (!VALID_STATUSES.contains(productDTO.getStatus())) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ. Phải là một trong: " + VALID_STATUSES);
        }

        // Kiểm tra danh mục đặc biệt nếu có
        if (productDTO.getSpecialCategory() != null && !productDTO.getSpecialCategory().trim().isEmpty() &&
            !VALID_SPECIAL_CATEGORIES.contains(productDTO.getSpecialCategory())) {
            return ResponseEntity.badRequest().body("Danh mục đặc biệt không hợp lệ. Phải là một trong: " + VALID_SPECIAL_CATEGORIES);
        }

        // Kiểm tra giá giảm và tỷ lệ giảm giá
        if (productDTO.getDiscountedPrice() < 0 || productDTO.getDiscount() < 0) {
            return ResponseEntity.badRequest().body("Giá giảm và tỷ lệ giảm giá không được âm");
        }

        // Kiểm tra định dạng URL hình ảnh nếu có
        if (productDTO.getImg() != null && !productDTO.getImg().trim().isEmpty()) {
            try {
                new URL(productDTO.getImg()).toURI();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("URL hình ảnh không hợp lệ");
            }
        }

        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo sản phẩm thành công");
            response.put("product", createdProduct);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo sản phẩm: " + e.getMessage());
        }
    }

    // Lấy danh sách tất cả sản phẩm
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductDTO> products = productService.getAllProducts();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lấy danh sách sản phẩm thành công");
            response.put("products", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID sản phẩm không hợp lệ");
        }

        try {
            ProductDTO product = productService.getProductById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lấy thông tin sản phẩm thành công");
            response.put("product", product);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Sản phẩm không tồn tại với ID: " + id);
        }
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        if (id == null || id <= 0 || productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
            productDTO.getCategory() == null || productDTO.getCategory().trim().isEmpty() ||
            productDTO.getStatus() == null || productDTO.getStatus().trim().isEmpty() ||
            productDTO.getOriginalPrice() <= 0) {
            return ResponseEntity.badRequest().body("ID sản phẩm, tên sản phẩm, danh mục, trạng thái hoặc giá gốc không hợp lệ");
        }

        // Kiểm tra trạng thái hợp lệ
        if (!VALID_STATUSES.contains(productDTO.getStatus())) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ. Phải là một trong: " + VALID_STATUSES);
        }

        // Kiểm tra danh mục đặc biệt nếu có
        if (productDTO.getSpecialCategory() != null && !productDTO.getSpecialCategory().trim().isEmpty() &&
            !VALID_SPECIAL_CATEGORIES.contains(productDTO.getSpecialCategory())) {
            return ResponseEntity.badRequest().body("Danh mục đặc biệt không hợp lệ. Phải là một trong: " + VALID_SPECIAL_CATEGORIES);
        }

        // Kiểm tra giá giảm và tỷ lệ giảm giá
        if (productDTO.getDiscountedPrice() < 0 || productDTO.getDiscount() < 0) {
            return ResponseEntity.badRequest().body("Giá giảm và tỷ lệ giảm giá không được âm");
        }

        // Kiểm tra định dạng URL hình ảnh nếu có
        if (productDTO.getImg() != null && !productDTO.getImg().trim().isEmpty()) {
            try {
                new URL(productDTO.getImg()).toURI();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("URL hình ảnh không hợp lệ");
            }
        }

        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cập nhật sản phẩm thành công");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Sản phẩm không tồn tại với ID: " + id);
        }
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID sản phẩm không hợp lệ");
        }

        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // Không trả về body cho mã 204
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Sản phẩm không Ascending không tồn tại với ID: " + id);
        }
    }
}