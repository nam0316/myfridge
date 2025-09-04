package com.example.test1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

public class HomeFragment extends Fragment {

    private TextView tvMonthYear;
    private TextView tvDay;
    private TextView tvDayOfWeek;
    private TextView tvFridgeStatus;
    private TextView tvRecommendedRecipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 파일을 inflate
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뷰 초기화
        initViews(view);

        // 데이터 설정
        setupHomeData();

        // 버튼 클릭 리스너 설정
        setupClickListeners();
    }

    private void initViews(View view) {
        try {
            tvMonthYear = view.findViewById(R.id.tv_month_year);
            tvDay = view.findViewById(R.id.tv_day);
            tvDayOfWeek = view.findViewById(R.id.tv_day_of_week);
            tvFridgeStatus = view.findViewById(R.id.tv_fridge_status);
            tvRecommendedRecipe = view.findViewById(R.id.tv_recommended_recipe);

            Log.d("HomeFragment", "Views initialized successfully");
        } catch (Exception e) {
            Log.e("HomeFragment", "Error initializing views: " + e.getMessage());
        }
    }

    private void setupHomeData() {
        // 달력 형태로 현재 날짜 설정
        setCalendarDate();

        // 냉장고 상태 로드
        loadFridgeStatus();

        // 추천 레시피 로드
        loadRecommendedRecipe();
    }

    private void setCalendarDate() {
        Date now = new Date();

        // 년월 (예: 2025년 8월)
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("yyyy년 M월", Locale.KOREAN);
        tvMonthYear.setText(monthYearFormat.format(now));

        // 일 (예: 31)
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.KOREAN);
        tvDay.setText(dayFormat.format(now));

        // 요일 (예: 일요일)
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.KOREAN);
        tvDayOfWeek.setText(dayOfWeekFormat.format(now));
    }

    private void setupClickListeners() {
        // 냉장고 현황 카드 클릭 (부모 LinearLayout을 클릭 가능하게)
        ((ViewGroup) tvFridgeStatus.getParent()).setOnClickListener(v -> {
            Log.d("HomeFragment", "냉장고 현황 카드 클릭");
            // TODO: 냉장고 Fragment로 이동
        });

        // 추천 레시피 카드 클릭 (부모 LinearLayout을 클릭 가능하게)
        ((ViewGroup) tvRecommendedRecipe.getParent()).setOnClickListener(v -> {
            Log.d("HomeFragment", "추천 레시피 카드 클릭");
            // TODO: 레시피 Fragment로 이동
        });
    }

    private void loadFridgeStatus() {
        // TODO: 실제 냉장고 데이터를 가져와서 설정
        // 임시 데이터
        String status = "• 보관중인 식재료: 12개\n• 유통기한 임박: 3개";
        tvFridgeStatus.setText(status);
    }

    private void loadRecommendedRecipe() {
        // TODO: 냉장고 재료를 기반으로 추천 레시피 생성
        // 임시 데이터
        String recipe = "김치찌개\n냉장고에 있는 김치와 돼지고기로 만들어보세요!";
        tvRecommendedRecipe.setText(recipe);
    }
}