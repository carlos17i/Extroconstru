package com.example.extrocontru;

import java.util.HashMap;
import java.util.Map;

public class Tool {
    private String toolId;
    private String toolName;
    private String category;
    private String subCategory;
    private String brand;
    private String model;
    private String description;
    private String price;
    private String stock;
    private String specifications;
    private String comments;
    private Map<String, String> images = new HashMap<>();
    private int quantity; // Agregar el campo de cantidad

    // Constructor vacío
    public Tool() {
        // Este constructor debe estar vacío
    }

    // Constructor sin cantidad
    public Tool(String toolId, String toolName, String category, String subCategory, String brand,
                String model, String description, String price, String stock, String specifications,
                String comments) {
        this.toolId = toolId;
        this.toolName = toolName;
        this.category = category;
        this.subCategory = subCategory;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.specifications = specifications;
        this.comments = comments;
        this.images = new HashMap<>();
    }

    // Constructor con cantidad
    public Tool(String toolId, String toolName, String category, String subCategory, String brand,
                String model, String description, String price, String stock, String specifications,
                String comments, int quantity) {
        this.toolId = toolId;
        this.toolName = toolName;
        this.category = category;
        this.subCategory = subCategory;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.price = price;
        this.stock = stock; // Puedes omitir este campo si no lo necesitas
        this.specifications = specifications;
        this.comments = comments;

        this.quantity = quantity;
    }


    // Getters y setters
    public String getToolId() { return toolId; }
    public void setToolId(String toolId) { this.toolId = toolId; }

    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public int getQuantity() { return quantity; } // Getter de cantidad
    public void setQuantity(int quantity) { this.quantity = quantity; } // Setter de cantidad
}
