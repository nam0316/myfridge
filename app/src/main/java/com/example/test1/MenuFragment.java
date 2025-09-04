package com.example.test1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

public class MenuFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private LinearLayout menuStatistics, menuFavorites, menuBackup;
    private LinearLayout menuNotifications, menuTheme, menuLanguage;
    private LinearLayout menuHelp, menuAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        loadUserInfo();
    }

    private void initViews(View view) {
        // 사용자 정보
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        // 앱 기능 메뉴들
        menuStatistics = view.findViewById(R.id.menu_statistics);
        menuFavorites = view.findViewById(R.id.menu_favorites);
        menuBackup = view.findViewById(R.id.menu_backup);

        // 설정 메뉴들
        menuNotifications = view.findViewById(R.id.menu_notifications);
        menuTheme = view.findViewById(R.id.menu_theme);
        menuLanguage = view.findViewById(R.id.menu_language);

        // 기타 메뉴들
        menuHelp = view.findViewById(R.id.menu_help);
        menuAbout = view.findViewById(R.id.menu_about);
    }

    private void setupClickListeners() {
        // 앱 기능 메뉴 클릭 리스너들
        menuStatistics.setOnClickListener(v -> {
            Log.d("MenuFragment", "통계 및 분석 메뉴 클릭");
            // TODO: 통계 화면으로 이동
        });

        menuFavorites.setOnClickListener(v -> {
            Log.d("MenuFragment", "즐겨찾는 레시피 메뉴 클릭");
            // TODO: 즐겨찾기 레시피 목록 화면으로 이동
        });

        menuBackup.setOnClickListener(v -> {
            Log.d("MenuFragment", "백업 및 동기화 메뉴 클릭");
            // TODO: 백업 설정 화면으로 이동
        });

        // 설정 메뉴 클릭 리스너들
        menuNotifications.setOnClickListener(v -> {
            Log.d("MenuFragment", "알림 설정 메뉴 클릭");
            // TODO: 알림 설정 화면으로 이동
        });

        menuTheme.setOnClickListener(v -> {
            Log.d("MenuFragment", "테마 및 표시 메뉴 클릭");
            // TODO: 테마 설정 화면으로 이동
        });

        menuLanguage.setOnClickListener(v -> {
            Log.d("MenuFragment", "언어 설정 메뉴 클릭");
            // TODO: 언어 설정 화면으로 이동
        });

        // 기타 메뉴 클릭 리스너들
        menuHelp.setOnClickListener(v -> {
            Log.d("MenuFragment", "도움말 메뉴 클릭");
            // TODO: 도움말 화면으로 이동
            showHelpDialog();
        });

        menuAbout.setOnClickListener(v -> {
            Log.d("MenuFragment", "앱 정보 메뉴 클릭");
            // TODO: 앱 정보 화면으로 이동
            showAboutDialog();
        });
    }

    private void loadUserInfo() {
        // TODO: 실제 사용자 정보를 로드해서 설정
        // SharedPreferences나 데이터베이스에서 사용자 정보 가져오기

        // 임시 데이터
        tvUserName.setText("김냉장");
        tvUserEmail.setText("fridge@example.com");
    }

    private void showHelpDialog() {
        // 간단한 도움말 메시지
        if (getContext() != null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("도움말");
            builder.setMessage("냉장고 관리 앱 사용법:\n\n" +
                    "1. 홈: 전체 현황을 확인하세요\n" +
                    "2. 냉장고: 식재료를 추가/관리하세요\n" +
                    "3. 레시피: 맛있는 요리를 찾아보세요\n" +
                    "4. 장보기: 필요한 식재료를 메모하세요\n" +
                    "5. 메뉴: 앱 설정을 변경하세요");
            builder.setPositiveButton("확인", null);
            builder.show();
        }
    }

    private void showAboutDialog() {
        // 앱 정보 다이얼로그
        if (getContext() != null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("앱 정보");
            builder.setMessage("냉장고 관리 앱\n\n" +
                    "버전: 1.0.0\n" +
                    "개발자: YourName\n" +
                    "출시일: 2024년 1월\n\n" +
                    "이 앱은 냉장고 식재료 관리, 레시피 추천, " +
                    "장보기 목록 관리 기능을 제공합니다.");
            builder.setPositiveButton("확인", null);
            builder.show();
        }
    }
}