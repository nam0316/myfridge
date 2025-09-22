package com.example.myapplication.ui.fridge;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FridgeAddActivity extends AppCompatActivity {

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
    private int quantity = 1;

    // 유통기한 입력
    private EditText etExpiryDate;
    private ImageView btnCalendar;
    private String selectedExpiryDate = "";

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

        initViews();
        setupEventListeners();
        setupIconRecyclerView();
        setupTabCategories();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);

        rvIcons = findViewById(R.id.rv_icons);
        ivSelectedProduct = findViewById(R.id.iv_selected_product);
        tvSelectedProduct = findViewById(R.id.tv_selected_product);

        chipGroupCategory = findViewById(R.id.chip_group_category);

        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        tvQuantity = findViewById(R.id.tv_quantity);

        etExpiryDate = findViewById(R.id.et_expiry_date);
        btnCalendar = findViewById(R.id.btn_calendar);

        btnCancel = findViewById(R.id.btn_cancel);
        btnAdd = findViewById(R.id.btn_add);

        tabCategories = findViewById(R.id.tab_categories);
        layoutSelectedItem = findViewById(R.id.layout_selected_item);
        tvSelectGuide = findViewById(R.id.tv_select_guide);
    }

    private void setupEventListeners() {
        // 검색 초기화 버튼
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(android.view.View.GONE);
            filterIcons("", getCurrentCategory());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
                filterIcons(s.toString(), getCurrentCategory());
            }
            @Override public void afterTextChanged(Editable s) {}
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

        // 캘린더 버튼과 유통기한 입력 필드
        btnCalendar.setOnClickListener(v -> showDatePicker());
        etExpiryDate.setOnClickListener(v -> showDatePicker());

        // 취소 버튼
        btnCancel.setOnClickListener(v -> finish());

        // 추가 버튼 (결과 전달)
        btnAdd.setOnClickListener(v -> {
            if (selectedIcon == null) {
                return; // 아이콘 선택 안 했을 때는 그냥 리턴
            }

            String productName = tvSelectedProduct.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", productName);
            resultIntent.putExtra("quantity", quantity);
            resultIntent.putExtra("expiryDate", selectedExpiryDate); // 유통기한 추가
            resultIntent.putExtra("iconResId", selectedIcon.getResId());
            resultIntent.putExtra("category", selectedIcon.getCategory());
            resultIntent.putExtra("unit", "개"); // 단위는 항상 "개"

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupIconRecyclerView() {
        allIconsList = loadAllIcons();
        filteredIconsList = new ArrayList<>(allIconsList);

        rvIcons.setLayoutManager(new GridLayoutManager(this, 4));
        iconAdapter = new IconAdapter(filteredIconsList, icon -> {
            selectedIcon = icon;

            tvSelectGuide.setVisibility(android.view.View.GONE);
            ivSelectedProduct.setImageResource(icon.getResId());
            tvSelectedProduct.setText(icon.getName());
            layoutSelectedItem.setVisibility(android.view.View.VISIBLE);

            selectCategoryChip(icon.getCategory());
            btnAdd.setEnabled(true);
        });
        rvIcons.setAdapter(iconAdapter);
    }

    private void setupTabCategories() {
        String[] categories = {"전체", "채소", "과일", "육류", "유제품", "냉동식품", "기타"};
        for (String category : categories) {
            tabCategories.addTab(tabCategories.newTab().setText(category));
        }

        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                filterIcons(etSearch.getText().toString(), tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // 캘린더 날짜 선택 다이얼로그
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 선택된 날짜를 Calendar 객체로 생성
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // 날짜 포맷팅 (yyyy-MM-dd)
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedExpiryDate = dateFormat.format(selectedDate.getTime());

                    // 사용자에게 보여줄 포맷 (MM월 dd일)
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
                    String displayDate = displayFormat.format(selectedDate.getTime());

                    etExpiryDate.setText(displayDate);
                },
                year, month, day
        );

        // 오늘 이후 날짜만 선택 가능하도록 설정
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
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

    private List<IconItem> loadAllIcons() {
        List<IconItem> icons = new ArrayList<>();
        icons.add(new IconItem(R.drawable.item_garlic, "마늘", "채소"));
        // TODO: 다른 아이콘들 추가 가능
        return icons;
    }

    private void selectCategoryChip(String category) {
        chipGroupCategory.clearCheck();
        switch (category) {
            case "채소": chipGroupCategory.check(R.id.chip_category_vegetables); break;
            case "과일": chipGroupCategory.check(R.id.chip_category_fruits); break;
            case "육류": chipGroupCategory.check(R.id.chip_category_meat); break;
            case "유제품": chipGroupCategory.check(R.id.chip_category_dairy); break;
            case "냉동식품": chipGroupCategory.check(R.id.chip_category_frozen); break;
            case "기타":
            default: chipGroupCategory.check(R.id.chip_category_etc); break;
        }
    }
}