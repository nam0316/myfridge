package com.example.test1;

import android.os.Parcel;
import android.os.Parcelable;

public class FridgeItem implements Parcelable {
    private int imageResId;
    private String name;
    private String storage;
    private int quantity;
    private String unit;
    private String expiry;

    // 생성자
    public FridgeItem(int imageResId, String name, String storage, int quantity, String unit, String expiry) {
        this.imageResId = imageResId;
        this.name = name != null ? name : "알 수 없는 상품";  // ✅ 생성자에서 null 체크
        this.storage = storage;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    // Getter 메서드들
    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        // ✅ 간단하게 name 값 그대로 반환 (생성자에서 이미 처리됨)
        return name;
    }

    public String getStorage() {
        return storage;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpiry() {
        return expiry;
    }

    // 수량과 단위를 함께 표시하는 메서드
    public String getQuantityText() {
        return quantity + (unit != null ? unit : "개");
    }

    // Setter 메서드들 (필요한 경우)
    public void setName(String name) {
        this.name = name != null ? name : "알 수 없는 상품";
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    // Parcelable 구현
    protected FridgeItem(Parcel in) {
        imageResId = in.readInt();
        name = in.readString();
        storage = in.readString();
        quantity = in.readInt();
        unit = in.readString();
        expiry = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageResId);
        dest.writeString(name);
        dest.writeString(storage);
        dest.writeInt(quantity);
        dest.writeString(unit);
        dest.writeString(expiry);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FridgeItem> CREATOR = new Creator<FridgeItem>() {
        @Override
        public FridgeItem createFromParcel(Parcel in) {
            return new FridgeItem(in);
        }

        @Override
        public FridgeItem[] newArray(int size) {
            return new FridgeItem[size];
        }
    };

    // 디버깅용 toString 메서드
    @Override
    public String toString() {
        return "FridgeItem{" +
                "imageResId=" + imageResId +
                ", name='" + name + '\'' +
                ", storage='" + storage + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", expiry='" + expiry + '\'' +
                '}';
    }
}