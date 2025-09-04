package com.example.test1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FridgeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FridgeAdapter adapter;
    private TextView tvSummary;

    private ArrayList<FridgeItem> allItems;      // 전체 아이템 리스트
    private ArrayList<FridgeItem> displayedItems; // 화면에 보여줄 리스트

    private ChipGroup chipGroup;

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

        // 샘플 데이터 추가 (유통기한이 임박한 것과 여유있는 것을 섞어서)
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "계란", "냉장", "10개", "D-3"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "양파", "실외", "3개", "신선함"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "우유", "냉장", "1L", "D-7"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "소고기", "냉동", "500g", "D-30"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "치즈", "냉장", "200g", "D-2"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "감자", "실외", "5개", "D-14"));
        allItems.add(new FridgeItem(R.drawable.ic_launcher_foreground, "아이스크림", "냉동", "1개", "D-60"));

        // displayedItems 초기화 (전체 아이템으로)
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
            // 기본적으로 "전체" 칩을 선택된 상태로 설정
            chipGroup.check(R.id.chip_all);

            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    filterItems(checkedIds.get(0));
                    updateSummary();
                }
            });
        }
    }

    private void filterItems(int checkedId) {
        if (displayedItems == null || allItems == null) {
            return;
        }

        displayedItems.clear();

        if (checkedId == R.id.chip_all) {
            displayedItems.addAll(allItems);
        } else if (checkedId == R.id.chip_fridge) {
            for (FridgeItem item : allItems) {
                if (item != null && item.getStorage() != null && item.getStorage().equals("냉장")) {
                    displayedItems.add(item);
                }
            }
        } else if (checkedId == R.id.chip_freezer) {
            for (FridgeItem item : allItems) {
                if (item != null && item.getStorage() != null && item.getStorage().equals("냉동")) {
                    displayedItems.add(item);
                }
            }
        } else if (checkedId == R.id.chip_room) {
            for (FridgeItem item : allItems) {
                if (item != null && item.getStorage() != null && item.getStorage().equals("실외")) {
                    displayedItems.add(item);
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupFloatingActionButton(View v) {
        FloatingActionButton fab = v.findViewById(R.id.btn_add_item);
        if (fab != null) {
            fab.setOnClickListener(view -> {
                // 상품 추가 다이얼로그나 새로운 액티비티로 이동
                // 임시로 샘플 아이템 추가하는 예시
                addSampleItem();
            });
        }
    }

    private void addSampleItem() {
        // 임시로 새 아이템을 추가하는 예시
        FridgeItem newItem = new FridgeItem(
                R.drawable.ic_launcher_foreground,
                "새 상품 " + (allItems.size() + 1),
                "냉장",
                "1개",
                "D-5"
        );

        allItems.add(newItem);

        // 현재 필터에 따라 displayedItems도 업데이트
        int checkedId = chipGroup.getCheckedChipId();
        filterItems(checkedId);

        updateSummary();
    }

    private void updateSummary() {
        if (tvSummary == null || allItems == null) {
            return;
        }

        int totalCount = allItems.size();
        int urgentCount = 0;

        // 유통기한이 임박한 아이템 개수 계산 (D-3 이하)
        for (FridgeItem item : allItems) {
            if (item != null && item.getExpiry() != null) {
                String expiry = item.getExpiry();
                if (expiry.startsWith("D-")) {
                    try {
                        int days = Integer.parseInt(expiry.substring(2));
                        if (days <= 3) {
                            urgentCount++;
                        }
                    } catch (NumberFormatException e) {
                        // 숫자가 아닌 경우 무시
                    }
                }
            }
        }

        String summaryText = "보관중 " + totalCount + "개 · 임박 " + urgentCount + "개";
        tvSummary.setText(summaryText);
    }

    // 외부에서 아이템을 추가할 때 사용할 메서드
    public void addItem(FridgeItem item) {
        if (allItems != null && item != null) {
            allItems.add(item);

            // 현재 필터에 맞으면 displayedItems에도 추가
            int checkedId = chipGroup.getCheckedChipId();
            boolean shouldAdd = false;

            if (checkedId == R.id.chip_all) {
                shouldAdd = true;
            } else if (checkedId == R.id.chip_fridge && item.getStorage() != null && item.getStorage().equals("냉장")) {
                shouldAdd = true;
            } else if (checkedId == R.id.chip_freezer && item.getStorage() != null && item.getStorage().equals("냉동")) {
                shouldAdd = true;
            } else if (checkedId == R.id.chip_room && item.getStorage() != null && item.getStorage().equals("실외")) {
                shouldAdd = true;
            }

            if (shouldAdd) {
                displayedItems.add(item);
                if (adapter != null) {
                    adapter.notifyItemInserted(displayedItems.size() - 1);
                }
            }

            updateSummary();
        }
    }

    // 외부에서 아이템을 삭제할 때 사용할 메서드
    public void removeItem(int position) {
        if (displayedItems != null && position >= 0 && position < displayedItems.size()) {
            FridgeItem itemToRemove = displayedItems.get(position);

            // allItems에서도 제거
            allItems.remove(itemToRemove);

            // displayedItems에서 제거
            displayedItems.remove(position);

            if (adapter != null) {
                adapter.notifyItemRemoved(position);
            }

            updateSummary();
        }
    }
}