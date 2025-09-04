package com.example.test1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private EditText etSearch;  // ✅ 검색바 추가
    private ImageView btnClearSearch;  // ✅ 검색 초기화 버튼
    private TabLayout tabCategories;
    private RecyclerView rvProducts;
    private LinearLayout layoutSelectedItem;
    private ImageView ivSelectedProduct;
    private TextView tvSelectedProduct;
    private ChipGroup chipGroupStorage;
    private TextView tvQuantity;
    private TextView tvExpiryDate;
    private Button btnIncrease;
    private Button btnDecrease;
    private Button btnAdd;
    private Button btnFinish;
    private RecyclerView rvAddedItems;

    // Data
    private ProductAdapter productAdapter;
    private AddedItemsAdapter addedItemsAdapter;
    private Product selectedProduct;
    private String[] categories;
    private ArrayList<FridgeItem> addedItems;
    private Spinner spinnerUnit;

    // 검색 관련 변수 추가
    private List<Product> allProducts;  // ✅ 전체 상품 목록
    private List<Product> filteredProducts;  // ✅ 필터링된 상품 목록
    private String currentCategory = "전체";  // ✅ 현재 선택된 카테고리
    private boolean isSearchMode = false;  // ✅ 검색 모드 여부

    // 날짜 관련 변수
    private Calendar selectedExpiryDate;
    private String currentExpiryFormat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_add_item);

        categories = Product.getCategories();
        addedItems = new ArrayList<>();

        // ✅ 전체 상품 목록 초기화
        initAllProducts();

        initViews();
        setupSearch();  // ✅ 검색 기능 설정
        setupTabs();
        setupProductRecyclerView();
        setupAddedItemsRecyclerView();
        setupListeners();

        // 첫 번째 카테고리 선택
        if (categories.length > 0) {
            currentCategory = categories[0];
            loadProducts(currentCategory);
        }
    }

    // ✅ 전체 상품 목록 초기화
    private void initAllProducts() {
        allProducts = new ArrayList<>();
        Product[] products = Product.getProductsByCategory("전체");
        allProducts.addAll(Arrays.asList(products));
        filteredProducts = new ArrayList<>(allProducts);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etSearch = findViewById(R.id.et_search);  // ✅ 검색바
        btnClearSearch = findViewById(R.id.btn_clear_search);  // ✅ 검색 초기화 버튼
        tabCategories = findViewById(R.id.tab_categories);
        rvProducts = findViewById(R.id.rv_products);
        layoutSelectedItem = findViewById(R.id.layout_selected_item);
        ivSelectedProduct = findViewById(R.id.iv_selected_product);
        tvSelectedProduct = findViewById(R.id.tv_selected_product);
        chipGroupStorage = findViewById(R.id.chip_group_storage);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvExpiryDate = findViewById(R.id.tv_expiry_date);
        btnIncrease = findViewById(R.id.btn_increase);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnAdd = findViewById(R.id.btn_add);
        btnFinish = findViewById(R.id.btn_finish);
        rvAddedItems = findViewById(R.id.rv_added_items);
        spinnerUnit = findViewById(R.id.spinner_unit);
    }

    // ✅ 검색 기능 설정
    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    filterProductsBySearch(query);

                    // 검색어가 있으면 클리어 버튼 표시
                    if (btnClearSearch != null) {
                        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // 검색 초기화 버튼
        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(v -> {
                etSearch.setText("");
                etSearch.clearFocus();
                isSearchMode = false;
                loadProducts(currentCategory);
            });
        }
    }

    // ✅ 검색어로 상품 필터링
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

        // 검색 결과가 없을 때 처리
        if (filteredProducts.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabs() {
        for (String category : categories) {
            tabCategories.addTab(tabCategories.newTab().setText(category));
        }

        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = tab.getText().toString();

                // ✅ 검색 모드가 아닐 때만 카테고리 변경 적용
                if (!isSearchMode) {
                    loadProducts(currentCategory);
                    hideSelectedItem();
                } else {
                    // 검색 모드일 때는 현재 검색 결과 유지하고 탭만 선택 상태 변경
                    Toast.makeText(AddItemActivity.this, "검색을 초기화하고 카테고리를 변경하세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupProductRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 4));

        productAdapter = new ProductAdapter(filteredProducts, product -> {
            selectedProduct = product;
            showSelectedItem(product);
            validateInput();
        });

        rvProducts.setAdapter(productAdapter);
    }

    private void setupAddedItemsRecyclerView() {
        rvAddedItems.setLayoutManager(new LinearLayoutManager(this));
        addedItemsAdapter = new AddedItemsAdapter(addedItems, position -> {
            addedItems.remove(position);
            addedItemsAdapter.notifyItemRemoved(position);
            updateAddedItemsVisibility();
            Toast.makeText(this, "아이템이 삭제되었습니다", Toast.LENGTH_SHORT).show();
        });
        rvAddedItems.setAdapter(addedItemsAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (!addedItems.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("added_items", addedItems);
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            if (selectedProduct != null && validateInput()) {
                addItemToList();
            }
        });

        btnFinish.setOnClickListener(v -> {
            if (!addedItems.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("added_items", addedItems);
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        });

        btnIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            tvQuantity.setText(String.valueOf(quantity + 1));
            validateInput();
        });

        btnDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 1) {
                tvQuantity.setText(String.valueOf(quantity - 1));
                validateInput();
            }
        });

        tvExpiryDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar today = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedExpiryDate = Calendar.getInstance();
                    selectedExpiryDate.set(year, month, dayOfMonth);

                    currentExpiryFormat = DateUtils.convertToExpiryFormat(selectedExpiryDate);
                    String displayDate = DateUtils.formatDateForDisplay(selectedExpiryDate);
                    String displayText = displayDate + " (" + currentExpiryFormat + ")";
                    tvExpiryDate.setText(displayText);

                    updateExpiryDateColor();
                    validateInput();
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateExpiryDateColor() {
        if (currentExpiryFormat.startsWith("D-")) {
            try {
                int days = Integer.parseInt(currentExpiryFormat.substring(2));
                if (days <= 3) {
                    tvExpiryDate.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (days <= 7) {
                    tvExpiryDate.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    tvExpiryDate.setTextColor(getResources().getColor(android.R.color.black));
                }
            } catch (NumberFormatException e) {
                tvExpiryDate.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else if ("만료됨".equals(currentExpiryFormat)) {
            tvExpiryDate.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if ("오늘".equals(currentExpiryFormat)) {
            tvExpiryDate.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    private void loadProducts(String category) {
        Product[] products = Product.getProductsByCategory(category);
        filteredProducts.clear();
        filteredProducts.addAll(Arrays.asList(products));
        productAdapter.updateProducts(filteredProducts);
    }

    private void showSelectedItem(Product product) {
        layoutSelectedItem.setVisibility(View.VISIBLE);
        ivSelectedProduct.setImageResource(product.getIconResId());
        tvSelectedProduct.setText(product.getName());

        tvQuantity.setText("1");
        tvExpiryDate.setText("날짜 선택");

        selectedExpiryDate = null;
        currentExpiryFormat = "";
        tvExpiryDate.setTextColor(getResources().getColor(android.R.color.black));

        setDefaultStorage(product.getCategory());
        validateInput();
    }

    private void hideSelectedItem() {
        layoutSelectedItem.setVisibility(View.GONE);
        selectedProduct = null;
        selectedExpiryDate = null;
        currentExpiryFormat = "";
        btnAdd.setEnabled(false);
    }

    private void setDefaultStorage(String category) {
        switch (category) {
            case "냉동식품":
                chipGroupStorage.check(R.id.chip_storage_freezer);
                break;
            case "채소":
            case "과일":
                chipGroupStorage.check(R.id.chip_storage_room);
                break;
            default:
                chipGroupStorage.check(R.id.chip_storage_fridge);
                break;
        }
    }

    private boolean validateInput() {
        String quantityStr = tvQuantity.getText().toString().trim();

        boolean isValid = selectedProduct != null &&
                !quantityStr.isEmpty() &&
                selectedExpiryDate != null &&
                !currentExpiryFormat.isEmpty();

        btnAdd.setEnabled(isValid);
        return isValid;
    }

    private void addItemToList() {
        int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());

        String unit = (spinnerUnit != null && spinnerUnit.getSelectedItem() != null)
                ? spinnerUnit.getSelectedItem().toString()
                : "개";

        Log.d("AddItemActivity", "Selected product name: " + (selectedProduct != null ? selectedProduct.getName() : "null"));

        FridgeItem newItem = new FridgeItem(
                selectedProduct.getIconResId(),
                selectedProduct.getName(),
                getSelectedStorage(),
                quantity,
                unit,
                currentExpiryFormat
        );

        Log.d("AddItemActivity", "Created item name: " + newItem.getName());

        addedItems.add(newItem);
        addedItemsAdapter.notifyItemInserted(addedItems.size() - 1);

        updateAddedItemsVisibility();

        Toast.makeText(this, "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();

        // 초기화
        tvQuantity.setText("1");
        tvExpiryDate.setText("날짜 선택");
        tvExpiryDate.setTextColor(getResources().getColor(android.R.color.black));
        selectedExpiryDate = null;
        currentExpiryFormat = "";
        btnAdd.setEnabled(false);

        rvAddedItems.smoothScrollToPosition(addedItems.size() - 1);
    }

    private void updateAddedItemsVisibility() {
        findViewById(R.id.layout_added_items).setVisibility(
                addedItems.isEmpty() ? LinearLayout.GONE : LinearLayout.VISIBLE
        );

        TextView tvAddedCount = findViewById(R.id.tv_added_count);
        if (tvAddedCount != null) {
            tvAddedCount.setText("추가된 상품 (" + addedItems.size() + "개)");
        }
    }

    private String getSelectedStorage() {
        int checkedId = chipGroupStorage.getCheckedChipId();
        if (checkedId == R.id.chip_storage_freezer) {
            return "냉동";
        } else if (checkedId == R.id.chip_storage_room) {
            return "실외";
        } else {
            return "냉장";
        }
    }

    @Override
    public void onBackPressed() {
        if (!addedItems.isEmpty()) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("added_items", addedItems);
            setResult(RESULT_OK, resultIntent);
        }
        super.onBackPressed();
    }
}