package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddShoppingItemActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private ImageView btnBack;
    private TabLayout tabCategories;
    private RecyclerView rvProducts;

    // 선택된 상품 정보 표시
    private View layoutSelectedItem;
    private ImageView ivSelectedProduct;
    private TextView tvSelectedProduct;
    private EditText etProductName;

    // 카테고리 선택
    private ChipGroup chipGroupCategory;

    // 수량 및 단위
    private TextView tvQuantity;
    private Button btnIncrease, btnDecrease;
    private Spinner spinnerUnit;

    // 가격 입력 (선택사항)
    private EditText etPrice;

    // 버튼
    private Button btnAdd, btnCancel;

    // 데이터
    private ProductAdapter productAdapter;
    private Product selectedProduct;
    private String[] categories;
    private String selectedDate;
    private int currentQuantity = 1;

    private EditText etSearch;
    private ImageView btnClearSearch;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private String currentCategory = "전체";
    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_add_shopping_item);

        // Intent에서 선택된 날짜 받기
        selectedDate = getIntent().getStringExtra("selected_date");

        categories = Product.getCategories();

        // 전체 상품 목록 초기화
        allProducts = new ArrayList<>();
        Product[] products = Product.getProductsByCategory("전체");
        allProducts.addAll(Arrays.asList(products));
        filteredProducts = new ArrayList<>(allProducts);

        initViews();
        setupSearch();  // ✅ 이 줄도 추가
        setupProductRecyclerView();
        setupListeners();

        // 첫 번째 카테고리 선택
        if (categories.length > 0) {
            loadProducts(categories[0]);
        }

        // 기본값 설정
        tvQuantity.setText("1");
        validateInput();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tabCategories = findViewById(R.id.tab_categories);
        rvProducts = findViewById(R.id.rv_products);

        etSearch = findViewById(R.id.et_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        layoutSelectedItem = findViewById(R.id.layout_selected_item);
        ivSelectedProduct = findViewById(R.id.iv_selected_product);
        tvSelectedProduct = findViewById(R.id.tv_selected_product);
        etProductName = findViewById(R.id.et_product_name);
        scrollView = findViewById(R.id.scrollView);
        chipGroupCategory = findViewById(R.id.chip_group_category);

        tvQuantity = findViewById(R.id.tv_quantity);
        btnIncrease = findViewById(R.id.btn_increase);
        btnDecrease = findViewById(R.id.btn_decrease);
        spinnerUnit = findViewById(R.id.spinner_unit);

        etPrice = findViewById(R.id.et_price);

        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);

        // RecyclerView 보이게 설정
        rvProducts.setVisibility(View.VISIBLE);
    }

    private void setupTabs() {
        for (String category : categories) {
            tabCategories.addTab(tabCategories.newTab().setText(category));
        }

        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                loadProducts(category);
                hideSelectedItem(); // 탭 변경시 선택 초기화
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupProductRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 4));

        productAdapter = new ProductAdapter(Arrays.asList(), product -> {
            selectedProduct = product;
            showSelectedItem(product);
            validateInput();
            scrollToQuantitySection(); // ✅ 자동 스크롤 추가
        });

        rvProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            if (validateInput()) {
                addShoppingItem();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            currentQuantity++;
            tvQuantity.setText(String.valueOf(currentQuantity));
            validateInput();
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
                validateInput();
            }
        });

        // 상품명 직접 입력시에도 검증
        etProductName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void loadProducts(String category) {
        Product[] products = Product.getProductsByCategory(category);
        productAdapter.updateProducts(Arrays.asList(products));
    }

    private void showSelectedItem(Product product) {
        layoutSelectedItem.setVisibility(View.VISIBLE);
        ivSelectedProduct.setImageResource(product.getIconResId());
        tvSelectedProduct.setText(product.getName());
        etProductName.setText(product.getName());

        // 카테고리에 따른 기본 선택
        setDefaultCategory(product.getCategory());

        validateInput();
    }

    private void hideSelectedItem() {
        selectedProduct = null;
        validateInput();
    }

    private void setDefaultCategory(String category) {
        switch (category) {
            case "채소":
                chipGroupCategory.check(R.id.chip_category_vegetables);
                break;
            case "야채":
                chipGroupCategory.check(R.id.chip_category_vegetables); // 야채도 채소로 매핑
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
            default:
                chipGroupCategory.check(R.id.chip_category_etc);
                break;
        }
    }

    private String getSelectedCategory() {
        int checkedId = chipGroupCategory.getCheckedChipId();
        if (checkedId == R.id.chip_category_vegetables) {
            return "채소";
        } else if (checkedId == R.id.chip_category_fruits) {
            return "과일";
        } else if (checkedId == R.id.chip_category_meat) {
            return "육류";
        } else if (checkedId == R.id.chip_category_dairy) {
            return "유제품";
        } else if (checkedId == R.id.chip_category_frozen) {
            return "냉동식품";
        } else {
            return "기타";
        }
    }

    private boolean validateInput() {
        String productName = etProductName.getText().toString().trim();
        String quantityStr = tvQuantity.getText().toString().trim();

        boolean isValid = !TextUtils.isEmpty(productName) &&
                !TextUtils.isEmpty(quantityStr) &&
                chipGroupCategory.getCheckedChipId() != -1;

        btnAdd.setEnabled(isValid);
        return isValid;
    }

    private void addShoppingItem() {
        String productName = etProductName.getText().toString().trim();
        int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
        String unit = spinnerUnit.getSelectedItem().toString();
        String category = getSelectedCategory();

        // 가격은 선택사항
        int price = 0;
        String priceStr = etPrice.getText().toString().trim();
        if (!TextUtils.isEmpty(priceStr)) {
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                price = 0;
            }
        }

        // 선택된 상품의 아이콘 또는 기본 아이콘
        int iconResId = selectedProduct != null ?
                selectedProduct.getIconResId() : R.drawable.ic_launcher_foreground;

        // 결과 Intent 생성
        Intent resultIntent = new Intent();
        resultIntent.putExtra("product_name", productName);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("quantity", quantity);
        resultIntent.putExtra("unit", unit);
        resultIntent.putExtra("price", price);
        resultIntent.putExtra("icon_res_id", iconResId);
        resultIntent.putExtra("selected_date", selectedDate);

        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, productName + "이(가) 장보기 목록에 추가되었습니다!", Toast.LENGTH_SHORT).show();

// ✅ 추가 후 폼 초기화 (종료하지 않음)
        resetForm();
    }

    // ✅ 수량 입력 부분으로 스크롤 이동하는 메서드 수정
    private void scrollToQuantitySection() {
        if (scrollView != null && layoutSelectedItem != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, layoutSelectedItem.getTop()));
        }
    }

    // ✅ 폼 초기화 메서드 수정
    private void resetForm() {
        // 선택된 상품 초기화
        selectedProduct = null;
        layoutSelectedItem.setVisibility(View.GONE);

        // 상품명 초기화
        etProductName.setText("");

        // 수량 초기화
        currentQuantity = 1;
        tvQuantity.setText("1");

        // 가격 초기화
        etPrice.setText("");

        // 카테고리를 기타로 초기화
        chipGroupCategory.check(R.id.chip_category_etc);

        // 유닛을 기본값(개)으로 초기화
        if (spinnerUnit != null && spinnerUnit.getAdapter() != null) {
            spinnerUnit.setSelection(0);
        }

        // ProductAdapter 선택 초기화
        if (productAdapter != null) {
            productAdapter.clearSelection();
        }

        // 스크롤을 맨 위로 이동
        if (scrollView != null) {
            scrollView.smoothScrollTo(0, 0);
        }

        // 입력 검증
        validateInput();
    }

    // ✅ 검색 기능 설정 (resetForm 밖으로 이동)
    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    filterProductsBySearch(query);

                    if (btnClearSearch != null) {
                        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(v -> {
                etSearch.setText("");
                etSearch.clearFocus();
                isSearchMode = false;
                loadProducts(currentCategory);
            });
        }
    }

    // ✅ 검색어로 상품 필터링 (resetForm 밖으로 이동)
    private void filterProductsBySearch(String query) {
        if (query.isEmpty()) {
            isSearchMode = false;
            loadProducts(currentCategory);
            return;
        }

        isSearchMode = true;
        filteredProducts.clear();

        String lowerQuery = query.toLowerCase();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowerQuery)) {
                filteredProducts.add(product);
            }
        }

        productAdapter.updateProducts(filteredProducts);
    }
}