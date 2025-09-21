package com.example.myapplication.ui.shopping;

public class ShoppingItem {
    private String name;
    private int price;
    private boolean isChecked;

    // 새로운 생성자 (name, price, isChecked)
    public ShoppingItem(String name, int price, boolean isChecked) {
        this.name = name;
        this.price = price;
        this.isChecked = isChecked;
    }

    // 기존 생성자 (isChecked 기본값 false)
    public ShoppingItem(String name, int price) {
        this(name, price, false);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
