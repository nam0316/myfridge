package com.example.myapplication.ui.fridge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FridgeItem {
    private String name;
    private String category;
    private int quantity;
    private boolean isChecked;
    private int iconResId;
    private String expiryDate; // yyyy-MM-dd 형식

    public FridgeItem(String name, String category, int quantity, boolean isChecked, int iconResId, String expiryDate) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.isChecked = isChecked;
        this.iconResId = iconResId;
        this.expiryDate = expiryDate;
    }

    public FridgeItem(String name, String category, int quantity, boolean isChecked, int iconResId) {
        this(name, category, quantity, isChecked, iconResId, "");
    }

    // Getter
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public boolean isChecked() { return isChecked; }
    public int getIconResId() { return iconResId; }
    public String getExpiryDate() { return expiryDate; }

    // ✅ 임박 여부 계산 (7일 이내)
    public boolean isNearExpire() {
        if (expiryDate == null || expiryDate.isEmpty()) return false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(expiryDate);

            Calendar today = Calendar.getInstance();
            Calendar expiryCal = Calendar.getInstance();
            expiryCal.setTime(expiry);

            Calendar sevenDaysLater = Calendar.getInstance();
            sevenDaysLater.add(Calendar.DAY_OF_MONTH, 7);

            return expiryCal.before(sevenDaysLater) || expiryCal.equals(sevenDaysLater);
        } catch (ParseException e) {
            return false;
        }
    }

    // ✅ 표시용 유통기한
    public String getDisplayExpiryDate() {
        if (expiryDate == null || expiryDate.isEmpty()) return "미설정";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
            Date date = input.parse(expiryDate);
            return output.format(date);
        } catch (ParseException e) {
            return expiryDate;
        }
    }
}
