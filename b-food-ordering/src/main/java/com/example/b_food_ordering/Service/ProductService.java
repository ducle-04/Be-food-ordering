package com.example.b_food_ordering.Service;

import com.example.b_food_ordering.Dto.ProductDTO;
import com.example.b_food_ordering.Entity.Product;
import com.example.b_food_ordering.Repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Ánh xạ từ Product sang ProductDTO
    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setDiscount(product.getDiscount());
        dto.setCategory(product.getCategory());
        dto.setImg(product.getImg());
        dto.setStatus(product.getStatus());
        dto.setSpecialCategory(product.getSpecialCategory());
        return dto;
    }

    // Ánh xạ từ ProductDTO sang Product
    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setDiscountedPrice(dto.getDiscountedPrice());
        product.setDiscount(dto.getDiscount());
        product.setCategory(dto.getCategory());
        product.setImg(dto.getImg());
        product.setStatus(dto.getStatus());
        product.setSpecialCategory(dto.getSpecialCategory());
        return product;
    }

    // Tạo sản phẩm mới
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Kiểm tra các trường bắt buộc
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
            productDTO.getCategory() == null || productDTO.getCategory().trim().isEmpty() ||
            productDTO.getStatus() == null || productDTO.getStatus().trim().isEmpty() ||
            productDTO.getOriginalPrice() <= 0) {
            throw new IllegalArgumentException("Tên sản phẩm, danh mục, trạng thái và giá gốc không được để trống hoặc không hợp lệ");
        }

        // Kiểm tra giá trị trạng thái hợp lệ
        if (!List.of("AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED").contains(productDTO.getStatus())) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ. Phải là một trong: AVAILABLE, OUT_OF_STOCK, DISCONTINUED");
        }

        // Kiểm tra danh mục đặc biệt nếu có
        if (productDTO.getSpecialCategory() != null && !productDTO.getSpecialCategory().trim().isEmpty() &&
            !List.of("FEATURED", "NEW", "BESTSELLER").contains(productDTO.getSpecialCategory())) {
            throw new IllegalArgumentException("Danh mục đặc biệt không hợp lệ. Phải là một trong: FEATURED, NEW, BESTSELLER");
        }

        // Kiểm tra giá giảm và tỷ lệ giảm giá
        if (productDTO.getDiscountedPrice() < 0 || productDTO.getDiscount() < 0) {
            throw new IllegalArgumentException("Giá giảm và tỷ lệ giảm giá không được âm");
        }

        Product product = toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return toDTO(savedProduct);
    }

    // Lấy tất cả sản phẩm
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy sản phẩm theo ID
    public ProductDTO getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return toDTO(product.get());
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }
    }

    // Cập nhật sản phẩm
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
            productDTO.getCategory() == null || productDTO.getCategory().trim().isEmpty() ||
            productDTO.getStatus() == null || productDTO.getStatus().trim().isEmpty() ||
            productDTO.getOriginalPrice() <= 0) {
            throw new IllegalArgumentException("Tên sản phẩm, danh mục, trạng thái và giá gốc không được để trống hoặc không hợp lệ");
        }

        // Kiểm tra giá trị trạng thái hợp lệ
        if (!List.of("AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED").contains(productDTO.getStatus())) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ. Phải là một trong: AVAILABLE, OUT_OF_STOCK, DISCONTINUED");
        }

        // Kiểm tra danh mục đặc biệt nếu có
        if (productDTO.getSpecialCategory() != null && !productDTO.getSpecialCategory().trim().isEmpty() &&
            !List.of("FEATURED", "NEW", "BESTSELLER").contains(productDTO.getSpecialCategory())) {
            throw new IllegalArgumentException("Danh mục đặc biệt không hợp lệ. Phải là một trong: FEATURED, NEW, BESTSELLER");
        }

        // Kiểm tra giá giảm và tỷ lệ giảm giá
        if (productDTO.getDiscountedPrice() < 0 || productDTO.getDiscount() < 0) {
            throw new IllegalArgumentException("Giá giảm và tỷ lệ giảm giá không được âm");
        }

        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setOriginalPrice(productDTO.getOriginalPrice());
            product.setDiscountedPrice(productDTO.getDiscountedPrice());
            product.setDiscount(productDTO.getDiscount());
            product.setCategory(productDTO.getCategory());
            product.setImg(productDTO.getImg());
            product.setStatus(productDTO.getStatus());
            product.setSpecialCategory(productDTO.getSpecialCategory());
            Product updatedProduct = productRepository.save(product);
            return toDTO(updatedProduct);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }
    }

    // Xóa sản phẩm
    public void deleteProduct(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }
    }
}