package com.example.extrocontru;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String toolId;
    private String toolName;
    private String description;
    private String price;
    private String brand;
    private String model;
    private String specifications;
    private String toolImageUrl;
    private int quantity;
    private boolean selected;

    public CartItem() {
        // Constructor vacío necesario para Firebase
    }

    protected CartItem(Parcel in) {
        toolId = in.readString();
        toolName = in.readString();
        description = in.readString();
        price = in.readString();
        brand = in.readString();
        model = in.readString();
        specifications = in.readString();
        toolImageUrl = in.readString();
        quantity = in.readInt();
        selected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toolId);
        dest.writeString(toolName);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(specifications);
        dest.writeString(toolImageUrl);
        dest.writeInt(quantity);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getters y Setters para todos los campos

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getToolImageUrl() {
        return toolImageUrl;
    }

    public void setToolImageUrl(String toolImageUrl) {
        this.toolImageUrl = toolImageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
