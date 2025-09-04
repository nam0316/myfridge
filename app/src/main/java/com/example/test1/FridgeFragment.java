package com.example.test1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FridgeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FridgeAdapter adapter;
    private TextView tvSummary;

    private ArrayList<FridgeItem> allItems;       // 전체 아이템 리스트
    private ArrayList<FridgeItem> displayedItems; // 화면에 보여줄 리스트

    private ChipGroup chipGroup;

    // Activity Result Launcher (하나만 유지)
    private ActivityResultLauncher<Intent> addItemLauncher;

    // 상수 정의
    private static final String STORAGE_FRIDGE = "냉장";
    private static final String STORAGE_FREEZER = "냉동";
    private static final String STORAGE_ROOM = "실외";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActivityResultLauncher 초기화
        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // ✅ Parcelable 리스트로 받기
                            ArrayList<FridgeItem> newItems =
                                    data.getParcelableArrayListExtra("added_items");

                            if (newItems != null && !newItems.isEmpty()) {
                                for (FridgeItem item : newItems) {
                                    addItem(item);
                                }
                            }
                        }
                    }
                }
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fridge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // View 초기화
        initViews(v);

        // 샘플 데이터 생성
        initSampleData();

        // 어댑터 설정
        setupRecyclerView();

        // 필터링 설정
        setupFiltering();

        // 플로팅 버튼 설정
        setupFloatingActionButton(v);

        // 요약 정보 업데이트
        updateSummary();
    }

    private void initViews(View v) {
        recyclerView = v.findViewById(R.id.rv_fridge_items);
        tvSummary = v.findViewById(R.id.tv_summary);
        chipGroup = v.findViewById(R.id.chip_group);
    }

    private void initSampleData() {
        allItems = new ArrayList<>();

        // 샘플 데이터
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "계란", STORAGE_FRIDGE, 10, "개", "D-3"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "양파", STORAGE_ROOM, 3, "개", "신선함"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "우유", STORAGE_FRIDGE, 1, "개", "D-7"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "소고기", STORAGE_FREEZER, 500, "g", "D-30"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "치즈", STORAGE_FRIDGE, 200, "g", "D-2"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "감자", STORAGE_ROOM, 5, "개", "D-14"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "아이스크림", STORAGE_FREEZER, 1, "개", "D-60"));


        displayedItems = new ArrayList<>(allItems);
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FridgeAdapter(displayedItems);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupFiltering() {
        if (chipGroup != null) {
            chipGroup.check(R.id.chip_all);

            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                filterItems(checkedId);
                updateSummary();
            });
        }
    }

    private void filterItems(int checkedId) {
        if (displayedItems == null || allItems == null) return;

        displayedItems.clear();

        if (checkedId == R.id.chip_all) {
            displayedItems.addAll(allItems);
        } else if (checkedId == R.id.chip_fridge) {
            for (FridgeItem item : allItems) {
                if (STORAGE_FRIDGE.equals(item.getStorage())) displayedItems.add(item);
            }
        } else if (checkedId == R.id.chip_freezer) {
            for (FridgeItem item : allItems) {
                if (STORAGE_FREEZER.equals(item.getStorage())) displayedItems.add(item);
            }
        } else if (checkedId == R.id.chip_room) {
            for (FridgeItem item : allItems) {
                if (STORAGE_ROOM.equals(item.getStorage())) displayedItems.add(item);
            }
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void setupFloatingActionButton(View v) {
        FloatingActionButton fab = v.findViewById(R.id.btn_add_item);
        if (fab != null) {
            fab.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), AddItemActivity.class);
                addItemLauncher.launch(intent);
            });
        }
    }

// FridgeFragment.java의 updateSummary() 메서드를 다음과 같이 수정하세요:

    private void updateSummary() {
        if (tvSummary == null || allItems == null) return;

        int totalCount = allItems.size();
        int urgentCount = 0;

        for (FridgeItem item : allItems) {
            if (item.getExpiry() != null && item.getExpiry().startsWith("D-")) {
                try {
                    int days = Integer.parseInt(item.getExpiry().substring(2));
                    if (days <= 3) urgentCount++;
                } catch (NumberFormatException ignored) {}
            }
        }

        // ✅ SpannableString을 사용하여 색상 적용
        String summaryText = "보관중 " + totalCount + "개 · 임박 " + urgentCount + "개";
        SpannableString spannableString = new SpannableString(summaryText);

        // "보관중 X개" 부분을 파란색으로
        String storedText = "보관중 " + totalCount + "개";
        int storedStart = summaryText.indexOf(storedText);
        if (storedStart != -1) {
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark)),
                    storedStart,
                    storedStart + storedText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // "임박 X개" 부분을 빨간색으로
        String urgentText = "임박 " + urgentCount + "개";
        int urgentStart = summaryText.indexOf(urgentText);
        if (urgentStart != -1) {
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark)),
                    urgentStart,
                    urgentStart + urgentText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        tvSummary.setText(spannableString);
    }

    public void addItem(FridgeItem newItem) {
        if (allItems == null || newItem == null) return;

        boolean found = false;
        for (FridgeItem item : allItems) {
            if (item.getName().equals(newItem.getName()) &&
                    item.getStorage().equals(newItem.getStorage()) &&
                    item.getExpiry().equals(newItem.getExpiry())) {

                // 기존 수량과 합치기
                item.setQuantity(item.getQuantity() + newItem.getQuantity());

                int displayedIndex = displayedItems.indexOf(item);
                if (displayedIndex != -1 && adapter != null) {
                    adapter.notifyItemChanged(displayedIndex);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            allItems.add(newItem);

            int checkedId = chipGroup.getCheckedChipId();
            boolean shouldAdd = checkedId == R.id.chip_all ||
                    (checkedId == R.id.chip_fridge && STORAGE_FRIDGE.equals(newItem.getStorage())) ||
                    (checkedId == R.id.chip_freezer && STORAGE_FREEZER.equals(newItem.getStorage())) ||
                    (checkedId == R.id.chip_room && STORAGE_ROOM.equals(newItem.getStorage()));

            if (shouldAdd) {
                displayedItems.add(newItem);
                if (adapter != null) adapter.notifyItemInserted(displayedItems.size() - 1);
            }
        }

        updateSummary();
    }

    public void removeItem(int position) {
        if (displayedItems != null && position >= 0 && position < displayedItems.size()) {
            FridgeItem itemToRemove = displayedItems.get(position);
            allItems.remove(itemToRemove);
            displayedItems.remove(position);
            if (adapter != null) adapter.notifyItemRemoved(position);
            updateSummary();
        }
    }
}
