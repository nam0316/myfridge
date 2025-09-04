package com.example.test1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragment 인스턴스들
    private HomeFragment homeFragment;
    private FridgeFragment fridgeFragment;
    private RecipeFragment recipeFragment;
    private ShoppingFragment shoppingFragment;
    private MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        // 뷰 초기화
        initViews();

        // Fragment 인스턴스 생성
        initFragments();

        // BottomNavigationView 리스너 설정
        setupBottomNavigation();

        // 기본 냉장고 화면 표시 (테스트용)
        showHomeScreen();

        Log.d("MainActivity", "앱 시작 완료");
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView == null) {
            Log.e("MainActivity", "BottomNavigationView가 null입니다!");
        } else {
            Log.d("MainActivity", "BottomNavigationView 찾기 성공");
            // 기본으로 냉장고 탭 선택 (테스트용)
            bottomNavigationView.setSelectedItemId(R.id.nav_fridge);
        }
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        fridgeFragment = new FridgeFragment();
        recipeFragment = new RecipeFragment();
        shoppingFragment = new ShoppingFragment();
        menuFragment = new MenuFragment();

        Log.d("MainActivity", "모든 Fragment 인스턴스 생성 완료");
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                String selectedTab = item.getTitle() != null ? item.getTitle().toString() : "알 수 없음";
                Log.d("MainActivity", "메뉴 아이템 클릭: " + selectedTab);

                if (item.getItemId() == R.id.nav_fridge) {
                    showFragment(fridgeFragment);
                    Log.d("MainActivity", "냉장고 탭 선택");
                    return true;
                } else if (item.getItemId() == R.id.nav_recipe) {
                    showFragment(recipeFragment);
                    Log.d("MainActivity", "레시피 탭 선택");
                    return true;
                } else if (item.getItemId() == R.id.nav_home) {
                    showFragment(homeFragment);
                    Log.d("MainActivity", "홈 탭 선택");
                    return true;
                } else if (item.getItemId() == R.id.nav_shopping) {
                    showFragment(shoppingFragment);
                    Log.d("MainActivity", "장보기 탭 선택");
                    return true;
                } else if (item.getItemId() == R.id.nav_menu) {
                    showFragment(menuFragment);
                    Log.d("MainActivity", "메뉴 탭 선택");
                    return true;
                }

                return false;
            });
        }
    }

    private void showFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("MainActivity", "Fragment가 null입니다!");
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // content_frame에 Fragment 교체
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();

        Log.d("MainActivity", "Fragment 교체 완료: " + fragment.getClass().getSimpleName());
    }

    private void showHomeScreen() {
        // 테스트를 위해 FridgeFragment를 바로 표시
        if (fridgeFragment != null) {
            showFragment(fridgeFragment);
            Log.d("MainActivity", "초기 FridgeFragment 표시");
        } else {
            Log.e("MainActivity", "FridgeFragment가 null입니다!");
        }
    }
}