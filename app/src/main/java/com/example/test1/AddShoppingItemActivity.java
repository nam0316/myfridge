package com.example.test1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;

public class AddShoppingItemActivity extends AppCompatActivity {

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
    private String selectedDate; // 장보기 날짜

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

        initViews();
        setupTabs();
        setupProductRecyclerView();
        setupListeners();

        // 첫 번째 카테고리 선택
        if (categories.length > 0) {
            loadProducts(categories[0]);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tabCategories = findViewById(R.id.tab_categories);
        rvProducts = findViewById(R.id.rv_products);

        layoutSelectedItem = findViewById(R.id.layout_selected_item);
        ivSelectedProduct = findViewById(R.id.iv_selected_product);
        tvSelectedProduct = findViewById(R.id.tv_selected_product);
        etProductName = findViewById(R.id.et_product_name);

        chipGroupCategory = findViewById(R.id.chip_group_category);

        tvQuantity = findViewById(R.id.tv_quantity);
        btnIncrease = findViewById(R.id.btn_increase);
        btnDecrease = findViewById(R.id.btn_decrease);
        spinnerUnit = findViewById(R.id.spinner_unit);

        etPrice = findViewById(R.id.et_price);

        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);
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


        private void loadProducts(String category) {
        Product[] products = Product.getProductsByCategory(category);
        productAdapter.updateProducts(Arrays.asList(products));
    }

    private void showSelectedItem(Product product) {
        layoutSelectedItem.setVisibility(View.VISIBLE);
        ivSelectedProduct.setImageResource(product.getIconResId());
        tvSelectedProduct.setText(product.getName());
        etProductName.setText(product.getName());

        // 기본값 설정
        tvQuantity.setText("1");
        etPrice.setText(""); // 가격은 선택사항이므로 비워둠

        // 카테고리에 따른 기본 선택
        setDefaultCategory(product.getCategory());

        validateInput();
    }

    private void setDefaultCategory(String category) {
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

        // 아이콘 리소스 ID (선택된 상품이 있으면 해당 아이콘, 없으면 기본 아이콘)
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

        Toast.makeText(this, "장보기 목록에 추가되었습니다!", Toast.LENGTH_SHORT).show();
        finish();
    }
}