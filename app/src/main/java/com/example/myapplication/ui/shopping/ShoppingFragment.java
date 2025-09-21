package com.example.myapplication.ui.shopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment implements ShoppingAdapter.OnItemCheckedListener {

    private static final int REQUEST_ADD_ITEM = 100;

    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private List<ShoppingItem> items = new ArrayList<>();

    private TextView tvItemSummary, tvTotalPrice;

    private FloatingActionButton btnAddItem;
    private Button btnClearCompleted;

    public ShoppingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        recyclerView = view.findViewById(R.id.rv_shopping_items);
        tvItemSummary = view.findViewById(R.id.tv_item_summary);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnAddItem = view.findViewById(R.id.btn_add_item);
        btnClearCompleted = view.findViewById(R.id.btn_clear_completed);

        adapter = new ShoppingAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // + 버튼 → 상품추가 페이지 이동
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddItemActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ITEM);
        });

        // 완료된 항목 삭제
        btnClearCompleted.setOnClickListener(v -> {
            adapter.removeCheckedItems();
            updateSummary();
        });

        return view;
    }

    @Override
    public void onItemChecked() {
        updateSummary();
    }

    // 상품추가 화면에서 결과 받기
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ITEM && resultCode == Activity.RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            int quantity = data.getIntExtra("quantity", 1);
            String unit = data.getStringExtra("unit");
            int price = data.getIntExtra("price", 0);

            // isChecked 기본값 = false
            ShoppingItem newItem = new ShoppingItem(name + " (" + quantity + unit + ")", price, false);
            items.add(newItem);
            adapter.notifyItemInserted(items.size() - 1);

            updateSummary();
        }
    }

    private void updateSummary() {
        int total = adapter.getUncheckedTotal();
        int checkedCount = 0;
        for (ShoppingItem item : items) {
            if (item.isChecked()) checkedCount++;
        }
        tvItemSummary.setText("총 " + items.size() + "개 중 " + checkedCount + "개 담음");
        tvTotalPrice.setText("총 금액: " + total + "원");
    }
}
