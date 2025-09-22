package com.example.myapplication.ui.fridge;

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

import com.example.myapplication.R;
import com.example.myapplication.ui.shopping.FridgeAddActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FridgeFragment extends Fragment {

    private TextView tvSummary;
    private ChipGroup chipGroup;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddItem;

    private FridgeAdapter adapter;
    private List<FridgeItem> itemList = new ArrayList<>();

    private ActivityResultLauncher<Intent> addItemLauncher;

    public FridgeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActivityResultLauncher 등록
        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("name");
                        String category = data.getStringExtra("category");
                        int quantity = data.getIntExtra("quantity", 1);
                        int iconResId = data.getIntExtra("iconResId", 0);
                        String expiryDate = data.getStringExtra("expiryDate");

                        // 아이템 추가
                        FridgeItem item = new FridgeItem(name, category, quantity, false, iconResId, expiryDate);
                        itemList.add(item);
                        adapter.notifyItemInserted(itemList.size() - 1);
                        updateSummary();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        // UI 연결
        tvSummary = view.findViewById(R.id.tv_summary);
        chipGroup = view.findViewById(R.id.chip_group);
        recyclerView = view.findViewById(R.id.rv_fridge_items);
        btnAddItem = view.findViewById(R.id.btn_add_item);

        // RecyclerView 초기화
        adapter = new FridgeAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // ChipGroup 선택 리스너
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String filter = "전체";
            if (checkedId != View.NO_ID) {
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    filter = selectedChip.getText().toString();
                }
            }
            filterItems(filter);
        });

        // 아이템 추가 버튼
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FridgeAddActivity.class);
            addItemLauncher.launch(intent);
        });

        updateSummary();
        return view;
    }

    // ✅ Chip에 따라 필터링
    private void filterItems(String filter) {
        List<FridgeItem> filtered = new ArrayList<>();
        for (FridgeItem item : itemList) {
            if (filter.equals("전체") || item.getCategory().equals(filter)) {
                filtered.add(item);
            }
        }
        adapter.updateItems(filtered);
    }

    // ✅ 보관중 / 임박 개수 갱신
    private void updateSummary() {
        int total = itemList.size();
        int nearExpire = 0;
        for (FridgeItem item : itemList) {
            if (item.isNearExpire()) {
                nearExpire++;
            }
        }
        tvSummary.setText("보관중 " + total + "개 · 임박 " + nearExpire + "개");
    }
}
