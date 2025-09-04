package com.example.test1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.*;

public class ShoppingFragment extends Fragment {

    private Button btnAddItem, btnClearCompleted, btnShareList, btnSelectDate;
    private TextView tvCurrentDate, tvTotalItems, tvEstimatedCost, tvEmptyState;
    private RecyclerView rvShoppingItems;
    private LinearLayout layoutStats;

    private String currentSelectedDate;
    private Map<String, List<ShoppingItem>> shoppingItemsByDate;
    private ShoppingItemsAdapter adapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
    private SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    private ActivityResultLauncher<Intent> addItemLauncher; // ✅ 선언 추가

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActivityResultLauncher 초기화
        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String productName = data.getStringExtra("product_name");
                        String category = data.getStringExtra("category");
                        int quantity = data.getIntExtra("quantity", 1);
                        String unit = data.getStringExtra("unit");
                        int price = data.getIntExtra("price", 0);
                        int iconResId = data.getIntExtra("icon_res_id", R.drawable.ic_launcher_foreground);

                        // 새 아이템 생성
                        ShoppingItem newItem = new ShoppingItem(
                                productName, category, price, quantity, false
                        );

                        // 현재 날짜의 목록에 추가
                        List<ShoppingItem> items = shoppingItemsByDate.get(currentSelectedDate);
                        if (items == null) {
                            items = new ArrayList<>();
                            shoppingItemsByDate.put(currentSelectedDate, items);
                        }
                        items.add(newItem);

                        // UI 업데이트
                        updateUI();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initData();
        setupClickListeners();
        updateCurrentDate();
        updateUI();
    }

    private void initViews(View view) {
        btnAddItem = view.findViewById(R.id.btn_add_item);
        btnClearCompleted = view.findViewById(R.id.btn_clear_completed);
        btnShareList = view.findViewById(R.id.btn_share_list);
        btnSelectDate = view.findViewById(R.id.btn_select_date);

        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvEstimatedCost = view.findViewById(R.id.tv_estimated_cost);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        rvShoppingItems = view.findViewById(R.id.rv_shopping_items);
        layoutStats = view.findViewById(R.id.layout_stats);

        // RecyclerView 설정
        rvShoppingItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShoppingItemsAdapter(new ArrayList<>(), this::onItemChecked, this::onItemLongClick);
        rvShoppingItems.setAdapter(adapter);
    }

    private void initData() {
        shoppingItemsByDate = new HashMap<>();

        Calendar today = Calendar.getInstance();
        String todayKey = keyFormat.format(today.getTime());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowKey = keyFormat.format(tomorrow.getTime());

        Calendar dayAfter = Calendar.getInstance();
        dayAfter.add(Calendar.DAY_OF_MONTH, 2);
        String dayAfterKey = keyFormat.format(dayAfter.getTime());

        // 오늘 샘플 데이터
        List<ShoppingItem> todayItems = new ArrayList<>();
        todayItems.add(new ShoppingItem("우유 1L", "유제품", 3000, 1, false));
        todayItems.add(new ShoppingItem("계란 30개", "유제품", 8000, 1, false));
        todayItems.add(new ShoppingItem("양파", "채소", 2000, 3, true));
        shoppingItemsByDate.put(todayKey, todayItems);

        // 내일 샘플 데이터
        List<ShoppingItem> tomorrowItems = new ArrayList<>();
        tomorrowItems.add(new ShoppingItem("사과", "과일", 5000, 5, false));
        tomorrowItems.add(new ShoppingItem("닭가슴살", "육류", 12000, 1, false));
        tomorrowItems.add(new ShoppingItem("당근", "채소", 3000, 2, false));
        shoppingItemsByDate.put(tomorrowKey, tomorrowItems);

        // 모레 샘플 데이터
        List<ShoppingItem> dayAfterItems = new ArrayList<>();
        dayAfterItems.add(new ShoppingItem("빵", "기타", 4000, 2, false));
        dayAfterItems.add(new ShoppingItem("요거트", "유제품", 6000, 4, false));
        shoppingItemsByDate.put(dayAfterKey, dayAfterItems);

        // 오늘을 기본 선택
        currentSelectedDate = todayKey;
    }

    private void setupClickListeners() {
        btnAddItem.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "아이템 추가 버튼 클릭");
            addNewItem();
        });

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnClearCompleted.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "완료 항목 삭제 버튼 클릭");
            clearCompletedItems();
            updateUI();
        });

        btnShareList.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "목록 공유 버튼 클릭");
            shareShoppingList();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);

                    currentSelectedDate = keyFormat.format(selectedCalendar.getTime());
                    updateCurrentDate();
                    updateUI();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateCurrentDate() {
        try {
            Date date = keyFormat.parse(currentSelectedDate);
            String displayDate = dateFormat.format(date);

            Calendar today = Calendar.getInstance();
            Calendar selected = Calendar.getInstance();
            selected.setTime(date);

            String dateText = displayDate;
            if (isSameDay(today, selected)) {
                dateText = "오늘 (" + displayDate + ")";
            } else {
                today.add(Calendar.DAY_OF_MONTH, 1);
                if (isSameDay(today, selected)) {
                    dateText = "내일 (" + displayDate + ")";
                } else {
                    today.add(Calendar.DAY_OF_MONTH, 1);
                    if (isSameDay(today, selected)) {
                        dateText = "모레 (" + displayDate + ")";
                    }
                }
            }

            tvCurrentDate.setText(dateText);
            btnSelectDate.setText("📅 날짜 변경");
        } catch (Exception e) {
            tvCurrentDate.setText("날짜 선택");
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void updateUI() {
        List<ShoppingItem> currentItems = shoppingItemsByDate.get(currentSelectedDate);

        if (currentItems == null || currentItems.isEmpty()) {
            rvShoppingItems.setVisibility(View.GONE);
            layoutStats.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            btnClearCompleted.setEnabled(false);
            btnShareList.setEnabled(false);
        } else {
            rvShoppingItems.setVisibility(View.VISIBLE);
            layoutStats.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            btnClearCompleted.setEnabled(true);
            btnShareList.setEnabled(true);

            adapter.updateItems(currentItems);
            updateStatistics();
        }
    }

    private void updateStatistics() {
        List<ShoppingItem> items = shoppingItemsByDate.get(currentSelectedDate);
        if (items == null) return;

        int totalItems = 0;
        int totalCost = 0;

        for (ShoppingItem item : items) {
            if (!item.isCompleted()) {
                totalItems += item.getQuantity();
                totalCost += item.getPrice() * item.getQuantity();
            }
        }

        tvTotalItems.setText(String.valueOf(totalItems));
        tvEstimatedCost.setText(String.format("%,d원", totalCost));
    }

    private void onItemChecked(ShoppingItem item, boolean isChecked) {
        item.setCompleted(isChecked);
        updateStatistics();
        Log.d("ShoppingFragment", item.getName() + " 체크상태: " + isChecked);
    }

    private void onItemLongClick(ShoppingItem item) {
        Log.d("ShoppingFragment", item.getName() + " 길게 눌림");
        // TODO: 삭제 다이얼로그 같은 기능 연결
    }

    private void addNewItem() {
        List<ShoppingItem> items = shoppingItemsByDate.get(currentSelectedDate);
        if (items == null) {
            items = new ArrayList<>();
            shoppingItemsByDate.put(currentSelectedDate, items);
        }

        items.add(new ShoppingItem("새 상품", "기타", 1000, 1, false));
        updateUI();
    }

    private void clearCompletedItems() {
        List<ShoppingItem> items = shoppingItemsByDate.get(currentSelectedDate);
        if (items == null) return;

        Iterator<ShoppingItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            ShoppingItem item = iterator.next();
            if (item.isCompleted()) {
                iterator.remove();
            }
        }
    }

    private void shareShoppingList() {
        List<ShoppingItem> items = shoppingItemsByDate.get(currentSelectedDate);
        if (items == null || items.isEmpty()) return;

        StringBuilder shareText = new StringBuilder();
        shareText.append(tvCurrentDate.getText()).append(" 장보기 목록\n\n");

        for (ShoppingItem item : items) {
            if (!item.isCompleted()) {
                shareText.append("• ").append(item.getName())
                        .append(" (").append(item.getQuantity()).append("개)")
                        .append(" - ").append(String.format("%,d원", item.getPrice()))
                        .append("\n");
            }
        }

        shareText.append("\n총 예상금액: ").append(tvEstimatedCost.getText());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "장보기 목록 공유"));
    }

    // ShoppingItem 내부 클래스
    public static class ShoppingItem {
        private String name;
        private String category;
        private int price;
        private int quantity;
        private boolean completed;

        public ShoppingItem(String name, String category, int price, int quantity, boolean completed) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
            this.completed = completed;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
}
