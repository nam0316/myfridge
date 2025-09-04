package com.example.test1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

public class RecipeFragment extends Fragment {

    private Button btnSearchRecipe;
    private Button btnCategoryAll, btnCategoryKorean, btnCategoryChinese, btnCategoryWestern, btnCategoryJapanese;
    private LinearLayout recipeCard1, recipeCard2, recipeCard3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        setupCategoryButtons();
    }

    private void initViews(View view) {
        btnSearchRecipe = view.findViewById(R.id.btn_search_recipe);

        // 카테고리 버튼들
        btnCategoryAll = view.findViewById(R.id.btn_category_all);
        btnCategoryKorean = view.findViewById(R.id.btn_category_korean);
        btnCategoryChinese = view.findViewById(R.id.btn_category_chinese);
        btnCategoryWestern = view.findViewById(R.id.btn_category_western);
        btnCategoryJapanese = view.findViewById(R.id.btn_category_japanese);

        // 레시피 카드들
        recipeCard1 = view.findViewById(R.id.recipe_card_1);
        recipeCard2 = view.findViewById(R.id.recipe_card_2);
        recipeCard3 = view.findViewById(R.id.recipe_card_3);
    }

    private void setupClickListeners() {
        // 검색 버튼
        btnSearchRecipe.setOnClickListener(v -> {
            Log.d("RecipeFragment", "레시피 검색 버튼 클릭");
            // TODO: 레시피 검색 기능 구현
        });

        // 레시피 카드 클릭 리스너
        recipeCard1.setOnClickListener(v -> {
            Log.d("RecipeFragment", "김치찌개 레시피 클릭");
            // TODO: 레시피 상세 화면으로 이동
        });

        recipeCard2.setOnClickListener(v -> {
            Log.d("RecipeFragment", "계란볶음밥 레시피 클릭");
            // TODO: 레시피 상세 화면으로 이동
        });

        recipeCard3.setOnClickListener(v -> {
            Log.d("RecipeFragment", "불고기 레시피 클릭");
            // TODO: 레시피 상세 화면으로 이동
        });
    }

    private void setupCategoryButtons() {
        // 카테고리 버튼 클릭 리스너
        btnCategoryAll.setOnClickListener(v -> selectCategory("전체"));
        btnCategoryKorean.setOnClickListener(v -> selectCategory("한식"));
        btnCategoryChinese.setOnClickListener(v -> selectCategory("중식"));
        btnCategoryWestern.setOnClickListener(v -> selectCategory("양식"));
        btnCategoryJapanese.setOnClickListener(v -> selectCategory("일식"));
    }

    private void selectCategory(String category) {
        Log.d("RecipeFragment", "카테고리 선택: " + category);

        // 모든 버튼을 기본색으로 변경
        resetCategoryButtonColors();

        // 선택된 카테고리 버튼 색상 변경
        Button selectedButton = null;
        switch (category) {
            case "전체":
                selectedButton = btnCategoryAll;
                break;
            case "한식":
                selectedButton = btnCategoryKorean;
                break;
            case "중식":
                selectedButton = btnCategoryChinese;
                break;
            case "양식":
                selectedButton = btnCategoryWestern;
                break;
            case "일식":
                selectedButton = btnCategoryJapanese;
                break;
        }

        if (selectedButton != null) {
            selectedButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_light, null));
        }

        // TODO: 선택된 카테고리에 따라 레시피 목록 필터링
        filterRecipesByCategory(category);
    }

    private void resetCategoryButtonColors() {
        int defaultColor = android.R.color.darker_gray;
        btnCategoryAll.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryKorean.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryChinese.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryWestern.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
        btnCategoryJapanese.setBackgroundTintList(getResources().getColorStateList(defaultColor, null));
    }

    private void filterRecipesByCategory(String category) {
        // TODO: 실제 레시피 데이터베이스에서 카테고리별 필터링
        Log.d("RecipeFragment", category + " 카테고리 레시피 로드");
    }
}