package com.example.myapplication.ui.shopping;

public class IconItem {
    private int resId;
    private String name;
    private String category;

    public IconItem(int resId, String name, String category) {
        this.resId = resId;
        this.name = name;
        this.category = category;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}