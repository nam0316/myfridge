package com.example.test1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

public class ShoppingFragment extends Fragment {

    private Button btnAddItem, btnClearCompleted, btnShareList;
    private Button btnCategoryAllShopping, btnCategoryVegetables, btnCategoryMeat, btnCategoryDairy, btnCategorySnacks;
    private TextView tvTotalItems, tvEstimatedCost;
    private CheckBox checkboxItem1, checkboxItem2, checkboxItem3;
    private LinearLayout shoppingItem1, shoppingItem2, shoppingItem3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        setupCategoryButtons();
        updateStatistics();
    }

    private void initViews(View view) {
        // 버튼들
        btnAddItem = view.findViewById(R.id.btn_add_item);
        btnClearCompleted = view.findViewById(R.id.btn_clear_completed);
        btnShareList = view.findViewById(R.id.btn_share_list);

        // 카테고리 버튼들
        btnCategoryAllShopping = view.findViewById(R.id.btn_category_all_shopping);
        btnCategoryVegetables = view.findViewById(R.id.btn_category_vegetables);
        btnCategoryMeat = view.findViewById(R.id.btn_category_meat);
        btnCategoryDairy = view.findViewById(R.id.btn_category_dairy);
        btnCategorySnacks = view.findViewById(R.id.btn_category_snacks);

        // 통계 텍스트뷰
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvEstimatedCost = view.findViewById(R.id.tv_estimated_cost);

        // 장보기 아이템들
        checkboxItem1 = view.findViewById(R.id.checkbox_item_1);
        checkboxItem2 = view.findViewById(R.id.checkbox_item_2);
        checkboxItem3 = view.findViewById(R.id.checkbox_item_3);

        shoppingItem1 = view.findViewById(R.id.shopping_item_1);
        shoppingItem2 = view.findViewById(R.id.shopping_item_2);
        shoppingItem3 = view.findViewById(R.id.shopping_item_3);
    }

    private void setupClickListeners() {
        // 아이템 추가 버튼
        btnAddItem.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "아이템 추가 버튼 클릭");
            // TODO: 새 아이템 추가 다이얼로그 표시
        });

        // 완료 항목 삭제 버튼
        btnClearCompleted.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "완료 항목 삭제 버튼 클릭");
            // TODO: 완료된 아이템들 삭제
            clearCompletedItems();
        });

        // 목록 공유 버튼
        btnShareList.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "목록 공유 버튼 클릭");
            // TODO: 장보기 목록 공유 기능
            shareShoppingList();
        });

        // 체크박스 리스너
        checkboxItem1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("ShoppingFragment", "우유 체크박스: " + isChecked);
            updateStatistics();
        });

        checkboxItem2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("ShoppingFragment", "계란 체크박스: " + isChecked);
            updateStatistics();
        });

        checkboxItem3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("ShoppingFragment", "양파 체크박스: " + isChecked);
            updateStatistics();
        });

        // 장보기 아이템 클릭 리스너
        shoppingItem1.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "우유 아이템 클릭");
            // TODO: 아이템 상세 정보 또는 편집
        });

        shoppingItem2.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "계란 아이템 클릭");
            // TODO: 아이템 상세 정보 또는 편집
        });

        shoppingItem3.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "양파 아이템 클릭");
            // TODO: 아이템 상세 정보 또는 편집
        });
    }

    private void setupCategoryButtons() {
        btnCategoryAllShopping.setOnClickListener(v -> selectShoppingCategory("전체"));
        btnCategoryVegetables.setOnClickListener(v -> selectShoppingCategory("채소"));
        btnCategoryMeat.setOnClickListener(v -> selectShoppingCategory("육류"));
        btnCategoryDairy.setOnClickListener(v -> selectShoppingCategory("유제품"));
        btnCategorySnacks.setOnClickListener(v -> selectShoppingCategory("간식"));
    }

    private void selectShoppingCategory(String category) {
        Log.d("ShoppingFragment", "장보기 카테고리 선택: " + category);

        // 모든 카테고리 버튼 색상 리셋
        resetShoppingCategoryColors();

        // 선택된 카테고리 버튼 하이라이트
        Button selectedButton = null;
        switch (category) {
            case "전체":
                selectedButton = btnCategoryAllShopping;
                break;
            case "채소":
                selectedButton = btnCategoryVegetables;
                break;
            case "육류":
                selectedButton = btnCategoryMeat;
                break;
            case "유제품":
                selectedButton = btnCategoryDairy;
                break;
            case "간식":
                selectedButton = btnCategorySnacks;
                break;
        }

        if (selectedButton != null) {
            selectedButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_light, null));
        }

        // TODO: 카테고리별 아이템 필터링
        filterItemsByCategory(category);
    }

    private void resetShoppingCategoryColors() {
        int defaultColor = android.R.color.darker_gray;
        btnCategoryAllShopping.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryVegetables.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryMeat.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryDairy.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategorySnacks.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
    }

    private void updateStatistics() {
        // 체크된 아이템 개수와 총 금액 계산
        int totalItems = 3; // 현재 총 아이템 수
        int completedItems = 0;
        int totalCost = 0;

        if (checkboxItem1.isChecked()) completedItems++;
        else totalCost += 3000; // 우유 가격

        if (checkboxItem2.isChecked()) completedItems++;
        else totalCost += 8000; // 계란 가격

        if (checkboxItem3.isChecked()) completedItems++;
        else totalCost += 2000; // 양파 가격

        // UI 업데이트
        tvTotalItems.setText(String.valueOf(totalItems - completedItems));
        tvEstimatedCost.setText(String.format("%,d원", totalCost));
    }

    private void clearCompletedItems() {
        // 완료된 아이템들을 목록에서 제거
        // TODO: 실제 데이터에서 완료된 아이템 삭제
        Log.d("ShoppingFragment", "완료된 아이템들 삭제 처리");
    }

    private void shareShoppingList() {
        // 장보기 목록을 다른 앱으로 공유
        // TODO: Intent를 사용해서 공유 기능 구현
        Log.d("ShoppingFragment", "장보기 목록 공유 기능");
    }

    private void filterItemsByCategory(String category) {
        // 선택된 카테고리에 따라 아이템 표시/숨김
        // TODO: 실제 아이템 데이터에서 카테고리별 필터링
        Log.d("ShoppingFragment", category + " 카테고리로 필터링");
    }
}