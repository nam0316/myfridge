package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductSelectionActivity extends AppCompatActivity
        implements ProductSelectionAdapter.OnProductSelectedListener {

    private static final String TAG = "ProductSelectionActivity";

    private TabLayout tabCategories;
    private RecyclerView rvProducts;
    private ChipGroup chipGroupStorage;
    private TextInputEditText etQuantity, etExpiry;
    private Button btnAdd, btnCancel, btnDone;
    private MaterialToolbar toolbar;

    private ProductSelectionAdapter productAdapter;
    private ArrayList<Product> allProducts;
    private ArrayList<Product> currentProducts;
    private ArrayList<Product> selectedProducts;

    // 장바구니(임시 저장)
    private ArrayList<FridgeItem> cartItems;

    // 카테고리별 상품 데이터
    private final String[] categories = {"전체", "육류", "야채", "과일", "유제품", "음료", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_selection);

        initViews();
        setupToolbar();
        setupTabs();
        initSampleData();
        setupRecyclerView();
        setupEventListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabCategories = findViewById(R.id.tab_categories);
        rvProducts = findViewById(R.id.rv_products);
        chipGroupStorage = findViewById(R.id.chip_group_storage);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiry = findViewById(R.id.et_expiry);
        btnAdd = findViewById(R.id.btn_add);       // "장바구니에 담기" 역할
        btnCancel = findViewById(R.id.btn_cancel); // 취소
        btnDone = findViewById(R.id.btn_finish);   // "완료" 버튼

        selectedProducts = new ArrayList<>();
        cartItems = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("상품 추가");
        }
    }

    private void setupTabs() {
        for (String category : categories) {
            tabCategories.addTab(tabCategories.newTab().setText(category));
        }

        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterProductsByCategory(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void initSampleData() {
        allProducts = new ArrayList<>();

        // ✅ Product 클래스 사용으로 변경
        // 육류
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "소고기", "육류"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "돼지고기", "육류"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "닭고기", "육류"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "계란", "육류"));

        // 야채
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "양파", "야채"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "당근", "야채"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "감자", "야채"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "배추", "야채"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "상추", "야채"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "오이", "야채"));

        // 과일
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "사과", "과일"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "바나나", "과일"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "오렌지", "과일"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "포도", "과일"));

        // 유제품
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "우유", "유제품"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "치즈", "유제품"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "요거트", "유제품"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "버터", "유제품"));

        // 음료
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "콜라", "음료"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "오렌지주스", "음료"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "물", "음료"));

        // 기타
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "빵", "기타"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "라면", "기타"));
        allProducts.add(new Product(R.drawable.ic_launcher_foreground, "아이스크림", "기타"));

        currentProducts = new ArrayList<>(allProducts);

        // 디버깅: 생성된 상품들 확인
        Log.d(TAG, "Total products created: " + allProducts.size());
        for (Product product : allProducts) {
            Log.d(TAG, "Product: " + product.getName() + ", Category: " + product.getCategory());
        }
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 4));
        productAdapter = new ProductSelectionAdapter(currentProducts, this);
        rvProducts.setAdapter(productAdapter);
    }

    private void setupEventListeners() {
        btnCancel.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> addToCart());
        if (btnDone != null) btnDone.setOnClickListener(v -> finishWithCart());

        chipGroupStorage.setOnCheckedChangeListener((group, checkedId) -> updateAddButtonState());
        TextWatcherHelper.addTextWatcher(etQuantity, text -> updateAddButtonState());
        TextWatcherHelper.addTextWatcher(etExpiry, text -> updateAddButtonState());
    }

    private void filterProductsByCategory(int categoryIndex) {
        currentProducts.clear();
        if (categoryIndex == 0) {
            currentProducts.addAll(allProducts);
        } else {
            String selectedCategory = categories[categoryIndex];
            for (Product product : allProducts) {  // ✅ Product 클래스로 변경
                if (product.getCategory().equals(selectedCategory)) {
                    currentProducts.add(product);
                }
            }
        }
        productAdapter.updateProducts(currentProducts);
    }

    @Override
    public void onProductSelected(Product product, boolean isSelected) {  // ✅ Product 클래스로 변경
        Log.d(TAG, "Product selected: " + product.getName() + ", isSelected: " + isSelected);

        if (isSelected) {
            if (!selectedProducts.contains(product)) selectedProducts.add(product);
        } else {
            selectedProducts.remove(product);
        }
        updateAddButtonState();
    }

    private void updateAddButtonState() {
        boolean hasSelectedProducts = !selectedProducts.isEmpty();
        boolean hasQuantity = !TextUtils.isEmpty(etQuantity.getText());
        boolean hasExpiry = !TextUtils.isEmpty(etExpiry.getText());
        boolean hasStorage = chipGroupStorage.getCheckedChipId() != View.NO_ID;

        btnAdd.setEnabled(hasSelectedProducts && hasQuantity && hasExpiry && hasStorage);
        if (btnDone != null) btnDone.setEnabled(!cartItems.isEmpty());
    }

    private void addToCart() {
        Log.d(TAG, "addToCart() called");
        Log.d(TAG, "Selected products count: " + selectedProducts.size());

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "상품을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityStr = etQuantity.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        if (TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(expiry)) {
            Toast.makeText(this, "수량과 유통기한을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String digits = quantityStr.replaceAll("[^0-9]", "");
        if (TextUtils.isEmpty(digits)) {
            Toast.makeText(this, "올바른 수량을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantityInt;
        try {
            quantityInt = Integer.parseInt(digits);
            if (quantityInt <= 0) {
                Toast.makeText(this, "수량은 1 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "수량을 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String storage = getSelectedStorage();
        if (storage == null) {
            Toast.makeText(this, "보관 방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int addedUnits = 0;
        for (Product product : new ArrayList<>(selectedProducts)) {  // ✅ Product 클래스로 변경
            Log.d(TAG, "Processing product: " + product.getName());

            String unit = getUnitForProduct(product.getName());
            Log.d(TAG, "Unit for " + product.getName() + ": " + unit);

            FridgeItem newItem = new FridgeItem(
                    product.getIconResId(),  // ✅ getIconResId()로 변경
                    product.getName(),
                    storage,
                    quantityInt,
                    unit,
                    expiry
            );

            Log.d(TAG, "Created FridgeItem: " + newItem.getName());

            boolean merged = mergeIntoCart(newItem);
            if (!merged) cartItems.add(newItem);
            addedUnits += quantityInt;
        }

        clearSelections();
        Toast.makeText(this, "장바구니에 담겼습니다 (" + addedUnits + "개, 항목 " + cartItems.size() + ")", Toast.LENGTH_SHORT).show();
        updateAddButtonState();

        // 장바구니 내용 로그 출력
        Log.d(TAG, "Cart items after adding:");
        for (FridgeItem item : cartItems) {
            Log.d(TAG, "Cart item: " + item.getName());
        }
    }

    private boolean mergeIntoCart(FridgeItem newItem) {
        for (FridgeItem item : cartItems) {
            if (item.getName().equals(newItem.getName())
                    && item.getStorage().equals(newItem.getStorage())
                    && item.getExpiry().equals(newItem.getExpiry())
                    && item.getUnit().equals(newItem.getUnit())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return true;
            }
        }
        return false;
    }

    private void finishWithCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "장바구니에 담긴 상품이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Finishing with cart. Items count: " + cartItems.size());
        for (FridgeItem item : cartItems) {
            Log.d(TAG, "Final cart item: " + item.getName());
        }

        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("added_items", cartItems);
        setResult(RESULT_OK, resultIntent);

        int totalUnits = 0;
        for (FridgeItem item : cartItems) totalUnits += item.getQuantity();
        Toast.makeText(this, "완료되었습니다 (" + totalUnits + "개, 항목 " + cartItems.size() + ")", Toast.LENGTH_SHORT).show();

        finish();
    }

    private String getSelectedStorage() {
        int checkedId = chipGroupStorage.getCheckedChipId();
        if (checkedId == R.id.chip_storage_fridge) return "냉장";
        else if (checkedId == R.id.chip_storage_freezer) return "냉동";
        else if (checkedId == R.id.chip_storage_room) return "실외";
        return null;
    }

    private void clearSelections() {
        for (Product product : selectedProducts) product.setSelected(false);  // ✅ Product 클래스로 변경
        selectedProducts.clear();
        etQuantity.setText("");
        etExpiry.setText("");
        productAdapter.notifyDataSetChanged();
        updateAddButtonState();
    }

    private String getUnitForProduct(String productName) {
        if (productName.contains("우유") || productName.contains("주스") ||
                productName.contains("음료") || productName.contains("물") ||
                productName.contains("콜라") || productName.contains("오렌지주스")) {
            return "ml";
        } else if (productName.contains("고기") || productName.contains("생선") ||
                productName.contains("치킨") || productName.contains("육류") ||
                productName.contains("소고기") || productName.contains("돼지고기") ||
                productName.contains("닭고기")) {
            return "g";
        } else if (productName.contains("빵") || productName.contains("케이크") ||
                productName.contains("아이스크림")) {
            return "조각";
        } else if (productName.contains("계란")) {
            return "개";
        } else if (productName.contains("치즈") || productName.contains("버터")) {
            return "g";
        } else {
            return "개";  // 기본값
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}