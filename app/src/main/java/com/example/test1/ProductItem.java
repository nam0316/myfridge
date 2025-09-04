package com.example.test1;

public class ProductItem {
    private String name;
    private int imageResId;
    private String category;
    private boolean selected = false; // 선택 상태 추가

    public ProductItem(String name, int imageResId, String category) {
        this.name = name;
        this.imageResId = imageResId;
        this.category = category;
    }

    // Getter 메서드들
    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getCategory() {
        return category;
    }

    public boolean isSelected() {
        return selected;
    }

    // Setter 메서드들
    public void setName(String name) {
        this.name = name;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}