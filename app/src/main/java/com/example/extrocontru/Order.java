package com.example.extrocontru;

import java.util.List;

public class Order {
    private String orderId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String estado;
    private String ciudad;
    private String codigoPostal;
    private String calle;
    private String cedula;
    private String referencia;
    private String rentalDuration;
    private List<CartItem> selectedItems;
    private String orderDate; // Nuevo campo para la fecha

    // Empty constructor is required for Firebase
    public Order() {
    }

    // Constructor
    public Order(String orderId, String nombre, String apellido, String telefono, String estado, String ciudad, String codigoPostal, String calle, String cedula, String referencia, String rentalDuration, List<CartItem> selectedItems, String orderDate) {
        this.orderId = orderId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.estado = estado;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.calle = calle;
        this.cedula = cedula;
        this.referencia = referencia;
        this.rentalDuration = rentalDuration;
        this.selectedItems = selectedItems;
        this.orderDate = orderDate; // Inicializa el campo de fecha
    }

    // Getters and setters for all fields
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(String rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public List<CartItem> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<CartItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
