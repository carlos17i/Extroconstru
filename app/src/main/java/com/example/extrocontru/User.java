package com.example.extrocontru;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String shippingAddress1;
    private String shippingAddress2;
    private List<CartItem> cartItems;
    private List<Order> orders;

    // Constructor vacío
    public User() {}

    // Constructor con parámetros
    public User(String userId, String name, String email, String phoneNumber,
                String shippingAddress1, String shippingAddress2,
                List<CartItem> cartItems, List<Order> orders) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress1 = shippingAddress1;
        this.shippingAddress2 = shippingAddress2;
        this.cartItems = cartItems;
        this.orders = orders;
    }

    // Getters y setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getShippingAddress1() { return shippingAddress1; }
    public void setShippingAddress1(String shippingAddress1) { this.shippingAddress1 = shippingAddress1; }

    public String getShippingAddress2() { return shippingAddress2; }
    public void setShippingAddress2(String shippingAddress2) { this.shippingAddress2 = shippingAddress2; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}
