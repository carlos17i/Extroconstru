package com.example.extrocontru;

public class OrderItem {
    private String toolId;
    private String toolName;
    private String price;
    private int quantity;

    // Constructor vacío
    public OrderItem() {}

    // Constructor con parámetros
    public OrderItem(String toolId, String toolName, String price, int quantity) {
        this.toolId = toolId;
        this.toolName = toolName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters y setters
    public String getToolId() { return toolId; }
    public void setToolId(String toolId) { this.toolId = toolId; }

    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Método para calcular el costo total del OrderItem
    public double getTotalPrice() {
        try {
            double priceValue = Double.parseDouble(price);
            return priceValue * quantity;
        } catch (NumberFormatException e) {
            // Manejar el caso en que el precio no se puede parsear
            return 0.0;
        }
    }
}
