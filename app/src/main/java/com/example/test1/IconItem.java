package com.example.test1;

public class IconItem {
    private String name;
    private int resourceId;

    public IconItem(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return name;
    }
}