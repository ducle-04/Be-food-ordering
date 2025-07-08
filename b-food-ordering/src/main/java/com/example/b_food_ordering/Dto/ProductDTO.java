package com.example.b_food_ordering.Dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double originalPrice;
    private double discountedPrice;
    private double discount;
    private String category;
    private String img;
    private String status; // Thêm trường trạng thái
    private String specialCategory; // Thêm trường danh mục đặc biệt

    public ProductDTO() {}

    public ProductDTO(Long id, String name, String description, double originalPrice, double discountedPrice, 
                     double discount, String category, String img, String status, String specialCategory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discount = discount;
        this.category = category;
        this.img = img;
        this.status = status;
        this.specialCategory = specialCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecialCategory() {
        return specialCategory;
    }

    public void setSpecialCategory(String specialCategory) {
        this.specialCategory = specialCategory;
    }
}