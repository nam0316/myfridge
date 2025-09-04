package com.example.test1;

public class FridgeItem {
    private int imageResId;
    private String name;
    private String storage;
    private String quantity;
    private String expiry;

    public FridgeItem(int imageResId, String name, String storage, String quantity, String expiry) {
        this.imageResId = imageResId;
        this.name = name;
        this.storage = storage;
        this.quantity = quantity;
        this.expiry = expiry;
    }

    // Getter 메서드들
    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getStorage() {
        return storage;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpiry() {
        return expiry;
    }

    // Setter 메서드들
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}