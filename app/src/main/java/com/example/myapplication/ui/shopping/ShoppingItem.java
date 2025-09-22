package com.example.myapplication.ui.shopping;

public class ShoppingItem {
    private String name;      // 상품 이름
    private int quantity;     // 수량
    private int price;        // 가격
    private boolean isChecked;
    private int iconResId;    // 아이콘 리소스 ID

    public ShoppingItem(String name, int quantity, int price, boolean isChecked, int iconResId) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isChecked = isChecked;
        this.iconResId = iconResId;
    }

    // Getter
    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getPrice() {
        return price;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public int getIconResId() {
        return iconResId;
    }

    // Setter
    public void setName(String name) {
        this.name = name;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
