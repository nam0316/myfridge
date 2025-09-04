package com.example.test1;

public class Product {
    private int iconResId;
    private String name;
    private String category;
    private boolean isSelected;

    public Product(int iconResId, String name, String category) {
        this.iconResId = iconResId;
        this.name = name;
        this.category = category;
        this.isSelected = false;
    }

    public int getIconResId() { return iconResId; }

    public String getName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return "알 수 없는 상품";
    }

    public String getCategory() { return category; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { this.isSelected = selected; }

    // ✅ 카테고리 순서 변경
    public static String[] getCategories() {
        return new String[]{"전체", "채소", "야채", "육류", "과일", "유제품", "기타"};
    }

    public static Product[] getProductsByCategory(String category) {
        switch (category) {
            case "전체":
                return getAllProducts();
            case "채소":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "양파", "채소"),
                        new Product(R.drawable.ic_launcher_foreground, "당근", "채소"),
                        new Product(R.drawable.ic_launcher_foreground, "감자", "채소"),
                        new Product(R.drawable.ic_launcher_foreground, "오이", "채소"),
                        new Product(R.drawable.ic_launcher_foreground, "배추", "채소"),
                        new Product(R.drawable.ic_launcher_foreground, "상추", "채소")
                };
            case "야채":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "브로콜리", "야채"),
                        new Product(R.drawable.ic_launcher_foreground, "파프리카", "야채"),
                        new Product(R.drawable.ic_launcher_foreground, "시금치", "야채")
                };
            case "육류":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "소고기", "육류"),
                        new Product(R.drawable.ic_launcher_foreground, "돼지고기", "육류"),
                        new Product(R.drawable.ic_launcher_foreground, "닭고기", "육류"),
                        new Product(R.drawable.ic_launcher_foreground, "계란", "육류")
                };
            case "과일":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "사과", "과일"),
                        new Product(R.drawable.ic_launcher_foreground, "바나나", "과일"),
                        new Product(R.drawable.ic_launcher_foreground, "오렌지", "과일"),
                        new Product(R.drawable.ic_launcher_foreground, "포도", "과일"),
                        new Product(R.drawable.ic_launcher_foreground, "딸기", "과일")
                };
            case "유제품":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "우유", "유제품"),
                        new Product(R.drawable.ic_launcher_foreground, "치즈", "유제품"),
                        new Product(R.drawable.ic_launcher_foreground, "요거트", "유제품"),
                        new Product(R.drawable.ic_launcher_foreground, "버터", "유제품")
                };
            case "기타":
                return new Product[]{
                        new Product(R.drawable.ic_launcher_foreground, "빵", "기타"),
                        new Product(R.drawable.ic_launcher_foreground, "라면", "기타"),
                        new Product(R.drawable.ic_launcher_foreground, "과자", "기타")
                };
            default:
                return new Product[0];
        }
    }

    private static Product[] getAllProducts() {
        return new Product[]{
                // 채소
                new Product(R.drawable.ic_launcher_foreground, "양파", "채소"),
                new Product(R.drawable.ic_launcher_foreground, "당근", "채소"),
                new Product(R.drawable.ic_launcher_foreground, "감자", "채소"),
                new Product(R.drawable.ic_launcher_foreground, "오이", "채소"),
                new Product(R.drawable.ic_launcher_foreground, "배추", "채소"),
                new Product(R.drawable.ic_launcher_foreground, "상추", "채소"),

                // 야채
                new Product(R.drawable.ic_launcher_foreground, "브로콜리", "야채"),
                new Product(R.drawable.ic_launcher_foreground, "파프리카", "야채"),
                new Product(R.drawable.ic_launcher_foreground, "시금치", "야채"),

                // 육류
                new Product(R.drawable.ic_launcher_foreground, "소고기", "육류"),
                new Product(R.drawable.ic_launcher_foreground, "돼지고기", "육류"),
                new Product(R.drawable.ic_launcher_foreground, "닭고기", "육류"),
                new Product(R.drawable.ic_launcher_foreground, "계란", "육류"),

                // 과일
                new Product(R.drawable.ic_launcher_foreground, "사과", "과일"),
                new Product(R.drawable.ic_launcher_foreground, "바나나", "과일"),
                new Product(R.drawable.ic_launcher_foreground, "오렌지", "과일"),
                new Product(R.drawable.ic_launcher_foreground, "포도", "과일"),
                new Product(R.drawable.ic_launcher_foreground, "딸기", "과일"),

                // 유제품
                new Product(R.drawable.ic_launcher_foreground, "우유", "유제품"),
                new Product(R.drawable.ic_launcher_foreground, "치즈", "유제품"),
                new Product(R.drawable.ic_launcher_foreground, "요거트", "유제품"),
                new Product(R.drawable.ic_launcher_foreground, "버터", "유제품"),

                // 기타
                new Product(R.drawable.ic_launcher_foreground, "빵", "기타"),
                new Product(R.drawable.ic_launcher_foreground, "라면", "기타"),
                new Product(R.drawable.ic_launcher_foreground, "과자", "기타")
        };
    }

    @Override
    public String toString() {
        return "Product{" +
                "iconResId=" + iconResId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
