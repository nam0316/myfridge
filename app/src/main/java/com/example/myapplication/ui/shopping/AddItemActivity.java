package com.example.myapplication.ui.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "AddItemActivity";

    // 뒤로가기 버튼
    private ImageView btnBack;

    // 검색 관련
    private EditText etSearch;
    private ImageView btnClearSearch;

    // 아이콘 선택 관련
    private RecyclerView rvIcons;
    private IconAdapter iconAdapter;
    private ImageView ivSelectedProduct;
    private TextView tvSelectedProduct;

    private ChipGroup chipGroupCategory;

    // 수량 관련
    private TextView btnDecrease, btnIncrease;
    private TextView tvQuantity;
    private Spinner spinnerUnit;
    private int quantity = 1;

    // 가격 입력
    private EditText etPrice;

    // 하단 버튼
    private Button btnCancel, btnAdd;

    // 카테고리 탭
    private TabLayout tabCategories;

    // 아이콘 데이터
    private List<IconItem> allIconsList;
    private List<IconItem> filteredIconsList;

    // 선택된 상품 정보 레이아웃
    private LinearLayout layoutSelectedItem;
    private TextView tvSelectGuide;

    // 현재 선택된 아이콘 추적
    private IconItem selectedIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // 초기화
        initViews();
        setupEventListeners();
        setupIconRecyclerView();
        setupTabCategories();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etSearch = findViewById(R.id.et_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);

        rvIcons = findViewById(R.id.rv_icons);
        ivSelectedProduct = findViewById(R.id.iv_selected_product);
        tvSelectedProduct = findViewById(R.id.tv_selected_product);

        chipGroupCategory = findViewById(R.id.chip_group_category);

        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        tvQuantity = findViewById(R.id.tv_quantity);
        spinnerUnit = findViewById(R.id.spinner_unit);

        etPrice = findViewById(R.id.et_price);

        btnCancel = findViewById(R.id.btn_cancel);
        btnAdd = findViewById(R.id.btn_add);

        tabCategories = findViewById(R.id.tab_categories);
        layoutSelectedItem = findViewById(R.id.layout_selected_item);
        tvSelectGuide = findViewById(R.id.tv_select_guide);
    }

    private void setupEventListeners() {
        // 뒤로가기
        btnBack.setOnClickListener(v -> finish());

        // 검색 관련
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            filterIcons("", getCurrentCategory());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                filterIcons(s.toString(), getCurrentCategory());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 수량 조절
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        // 취소 버튼
        btnCancel.setOnClickListener(v -> finish());

        // 추가 버튼 (결과 전달)
        btnAdd.setOnClickListener(v -> {
            // 아이콘이 선택되었는지 확인
            if (layoutSelectedItem.getVisibility() == View.GONE) {
                // 아이콘이 선택되지 않은 경우 처리 (토스트 메시지 등)
                return;
            }

            String priceStr = etPrice.getText().toString().trim();
            int selectedCategoryId = chipGroupCategory.getCheckedChipId();

            // 가격 값 처리
            int price = 0;
            if (!priceStr.isEmpty()) {
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    price = 0;
                }
            }

            // 단위
            String unit = spinnerUnit.getSelectedItem().toString();

            // 아이콘에서 선택된 이름 사용
            String productName = tvSelectedProduct.getText().toString();

            // 결과 Intent
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", productName);
            resultIntent.putExtra("quantity", quantity);
            resultIntent.putExtra("unit", unit);
            resultIntent.putExtra("price", price);
            resultIntent.putExtra("iconResId", selectedIcon.getResId()); // 아이콘 리소스 ID 추가
            resultIntent.putExtra("category", selectedIcon.getCategory()); // 카테고리도 추가

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupIconRecyclerView() {
        // 아이콘 목록 불러오기
        allIconsList = loadAllIcons();
        filteredIconsList = new ArrayList<>(allIconsList);

        rvIcons.setLayoutManager(new GridLayoutManager(this, 4));
        iconAdapter = new IconAdapter(filteredIconsList, icon -> {
            // 같은 아이콘을 다시 클릭한 경우 선택 해제
            if (selectedIcon != null && selectedIcon.getResId() == icon.getResId()) {
                // 원상복구
                resetSelection();
                return;
            }

            // 새로운 아이콘 선택
            selectedIcon = icon;

            // 선택 안내 텍스트 숨기기
            tvSelectGuide.setVisibility(View.GONE);

            // 아이콘 선택 시 이미지와 텍스트 표시
            ivSelectedProduct.setImageResource(icon.getResId());
            tvSelectedProduct.setText(icon.getName());

            // 선택된 상품 정보 영역 표시 (카테고리, 수량, 가격 입력란 포함)
            layoutSelectedItem.setVisibility(View.VISIBLE);

            // 선택된 카테고리에 맞게 Chip 자동 선택
            selectCategoryChip(icon.getCategory());

            btnAdd.setEnabled(true); // 아이콘 선택 시 추가 버튼 활성화
        });
        rvIcons.setAdapter(iconAdapter);
    }

    private void setupTabCategories() {
        // 카테고리 탭 추가
        String[] categories = {"전체", "채소", "과일", "육류", "유제품", "냉동식품", "기타"};

        for (String category : categories) {
            TabLayout.Tab tab = tabCategories.newTab().setText(category);
            tabCategories.addTab(tab);
        }

        // 탭이 추가된 후 레이아웃 조정 - 탭 간격 더 증가
        tabCategories.post(() -> {
            for (int i = 0; i < tabCategories.getTabCount(); i++) {
                TabLayout.Tab tab = tabCategories.getTabAt(i);
                if (tab != null && tab.view != null) {
                    // 좌우 패딩을 더 크게 해서 텍스트 잘림 방지
                    tab.view.setPadding(28, 14, 28, 14);

                    // 탭 간격을 더 크게 설정
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.view.getLayoutParams();
                    if (params != null) {
                        params.rightMargin = 12; // 오른쪽 마진을 8dp → 12dp로 증가
                        if (i == tabCategories.getTabCount() - 1) {
                            // 마지막 탭(기타)의 경우 오른쪽 마진을 더 크게
                            params.rightMargin = 16;
                        }
                        tab.view.setLayoutParams(params);
                    }
                }
            }
        });

        // 탭 선택 리스너
        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String searchText = etSearch.getText().toString();
                filterIcons(searchText, tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private String getCurrentCategory() {
        TabLayout.Tab selectedTab = tabCategories.getTabAt(tabCategories.getSelectedTabPosition());
        return selectedTab != null ? selectedTab.getText().toString() : "전체";
    }

    private void filterIcons(String searchText, String category) {
        filteredIconsList.clear();

        for (IconItem icon : allIconsList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    icon.getName().toLowerCase().contains(searchText.toLowerCase());
            boolean matchesCategory = category.equals("전체") ||
                    icon.getCategory().equals(category);

            if (matchesSearch && matchesCategory) {
                filteredIconsList.add(icon);
            }
        }

        iconAdapter.notifyDataSetChanged();
    }

    // 모든 아이콘 목록 (카테고리별로 구성)
    private List<IconItem> loadAllIcons() {
        List<IconItem> icons = new ArrayList<>();

        // 채소
        icons.add(new IconItem(R.drawable.item_garlic, "마늘", "채소"));
//        icons.add(new IconItem(R.drawable.item_onion, "양파", "채소"));
//        icons.add(new IconItem(R.drawable.item_carrot, "당근", "채소"));
//        icons.add(new IconItem(R.drawable.item_cabbage, "배추", "채소"));
//        icons.add(new IconItem(R.drawable.item_spinach, "시금치", "채소"));
//        icons.add(new IconItem(R.drawable.item_lettuce, "상추", "채소"));
//        icons.add(new IconItem(R.drawable.item_cucumber, "오이", "채소"));
//        icons.add(new IconItem(R.drawable.item_tomato, "토마토", "채소"));
//        icons.add(new IconItem(R.drawable.item_potato, "감자", "채소"));
//        icons.add(new IconItem(R.drawable.item_sweet_potato, "고구마", "채소"));
//
//        // 과일
//        icons.add(new IconItem(R.drawable.item_apple, "사과", "과일"));
//        icons.add(new IconItem(R.drawable.item_banana, "바나나", "과일"));
//        icons.add(new IconItem(R.drawable.item_orange, "오렌지", "과일"));
//        icons.add(new IconItem(R.drawable.item_grape, "포도", "과일"));
//        icons.add(new IconItem(R.drawable.item_strawberry, "딸기", "과일"));
//        icons.add(new IconItem(R.drawable.item_watermelon, "수박", "과일"));
//        icons.add(new IconItem(R.drawable.item_melon, "멜론", "과일"));
//        icons.add(new IconItem(R.drawable.item_pear, "배", "과일"));
//        icons.add(new IconItem(R.drawable.item_peach, "복숭아", "과일"));
//        icons.add(new IconItem(R.drawable.item_kiwi, "키위", "과일"));
//
//        // 육류
//        icons.add(new IconItem(R.drawable.item_beef, "소고기", "육류"));
//        icons.add(new IconItem(R.drawable.item_pork, "돼지고기", "육류"));
//        icons.add(new IconItem(R.drawable.item_chicken, "닭고기", "육류"));
//        icons.add(new IconItem(R.drawable.item_fish, "생선", "육류"));
//        icons.add(new IconItem(R.drawable.item_shrimp, "새우", "육류"));
//        icons.add(new IconItem(R.drawable.item_squid, "오징어", "육류"));
//        icons.add(new IconItem(R.drawable.item_crab, "게", "육류"));
//        icons.add(new IconItem(R.drawable.item_egg, "달걀", "육류"));
//
//        // 유제품
//        icons.add(new IconItem(R.drawable.item_milk, "우유", "유제품"));
//        icons.add(new IconItem(R.drawable.item_cheese, "치즈", "유제품"));
//        icons.add(new IconItem(R.drawable.item_yogurt, "요구르트", "유제품"));
//        icons.add(new IconItem(R.drawable.item_butter, "버터", "유제품"));
//        icons.add(new IconItem(R.drawable.item_cream, "생크림", "유제품"));
//
//        // 냉동식품
//        icons.add(new IconItem(R.drawable.item_ice_cream, "아이스크림", "냉동식품"));
//        icons.add(new IconItem(R.drawable.item_frozen_dumpling, "냉동만두", "냉동식품"));
//        icons.add(new IconItem(R.drawable.item_frozen_pizza, "냉동피자", "냉동식품"));
//        icons.add(new IconItem(R.drawable.item_frozen_fruit, "냉동과일", "냉동식품"));
//        icons.add(new IconItem(R.drawable.item_frozen_vegetable, "냉동야채", "냉동식품"));
//
//        // 기타
//        icons.add(new IconItem(R.drawable.item_rice, "쌀", "기타"));
//        icons.add(new IconItem(R.drawable.item_bread, "빵", "기타"));
//        icons.add(new IconItem(R.drawable.item_noodle, "면", "기타"));
//        icons.add(new IconItem(R.drawable.item_oil, "기름", "기타"));
//        icons.add(new IconItem(R.drawable.item_salt, "소금", "기타"));
//        icons.add(new IconItem(R.drawable.item_sugar, "설탕", "기타"));
//        icons.add(new IconItem(R.drawable.item_sauce, "소스", "기타"));
//        icons.add(new IconItem(R.drawable.item_seasoning, "조미료", "기타"));

        return icons;
    }

    // 선택 해제 (원상복구)
    private void resetSelection() {
        selectedIcon = null;

        // 선택된 상품 정보 영역 숨기기
        layoutSelectedItem.setVisibility(View.GONE);

        // 선택 안내 텍스트 다시 보이기
        tvSelectGuide.setVisibility(View.VISIBLE);

        // 추가 버튼 비활성화
        btnAdd.setEnabled(false);

        // 수량을 1로 초기화
        quantity = 1;
        tvQuantity.setText("1");

        // 가격 입력 필드 초기화
        etPrice.setText("");

        // 카테고리 선택 초기화 (기타로 설정)
        chipGroupCategory.clearCheck();
        findViewById(R.id.chip_category_etc).setSelected(true);
    }

    // 카테고리에 맞는 Chip 자동 선택
    private void selectCategoryChip(String category) {
        chipGroupCategory.clearCheck();

        switch (category) {
            case "채소":
                chipGroupCategory.check(R.id.chip_category_vegetables);
                break;
            case "과일":
                chipGroupCategory.check(R.id.chip_category_fruits);
                break;
            case "육류":
                chipGroupCategory.check(R.id.chip_category_meat);
                break;
            case "유제품":
                chipGroupCategory.check(R.id.chip_category_dairy);
                break;
            case "냉동식품":
                chipGroupCategory.check(R.id.chip_category_frozen);
                break;
            case "기타":
            default:
                chipGroupCategory.check(R.id.chip_category_etc);
                break;
        }
    }
}