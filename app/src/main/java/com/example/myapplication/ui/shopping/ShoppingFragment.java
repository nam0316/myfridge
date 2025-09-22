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
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShoppingFragment extends Fragment implements ShoppingAdapter.OnItemCheckedListener {

    private static final int REQUEST_ADD_ITEM = 100;

    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private List<ShoppingItem> items = new ArrayList<>();

    private TextView tvItemSummary, tvTotalPrice;
    private TextView tvCurrentDate, btnPrevDate, btnNextDate;

    private FloatingActionButton btnAddItem;
    private View btnClearCompleted;

    // ✅ 날짜 관련
    private Calendar currentCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

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

        // ✅ 날짜 UI
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        btnPrevDate = view.findViewById(R.id.btn_prev_date);
        btnNextDate = view.findViewById(R.id.btn_next_date);

        // ✅ 오늘 날짜로 초기화
        currentCalendar = Calendar.getInstance();
        updateDateText();

        // ✅ 날짜 버튼 이벤트
        btnPrevDate.setOnClickListener(v -> {
            currentCalendar.add(Calendar.DAY_OF_MONTH, -1); // 하루 전
            updateDateText();
        });

        btnNextDate.setOnClickListener(v -> {
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1); // 하루 후
            updateDateText();
        });

        adapter = new ShoppingAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // ✅ 아이템 클릭 시 다이얼로그로 수정
        adapter.setOnItemClickListener(position -> {
            ShoppingItem item = items.get(position);

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_item, null);
            EditText etName = dialogView.findViewById(R.id.et_edit_name);
            EditText etPrice = dialogView.findViewById(R.id.et_edit_price);
            TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
            TextView btnMinus = dialogView.findViewById(R.id.btn_decrease);
            TextView btnPlus = dialogView.findViewById(R.id.btn_increase);

            // ✅ 순서: 이름 → 가격 → 수량
            etName.setText(item.getName());
            etPrice.setText(String.valueOf(item.getPrice()));
            tvQuantity.setText(String.valueOf(item.getQuantity()));

            // 수량 - 버튼
            btnMinus.setOnClickListener(v -> {
                int q = Integer.parseInt(tvQuantity.getText().toString());
                if (q > 1) {
                    q--;
                    tvQuantity.setText(String.valueOf(q));
                }
            });

            // 수량 + 버튼
            btnPlus.setOnClickListener(v -> {
                int q = Integer.parseInt(tvQuantity.getText().toString());
                q++;
                tvQuantity.setText(String.valueOf(q));
            });

            new MaterialAlertDialogBuilder(getContext(), R.style.CustomAlertDialog)
                    .setTitle("상품 수정")
                    .setView(dialogView)
                    .setPositiveButton("저장", (dialog, which) -> {
                        String newName = etName.getText().toString().trim();
                        int newPrice = etPrice.getText().toString().isEmpty()
                                ? 0 : Integer.parseInt(etPrice.getText().toString());
                        int newQuantity = Integer.parseInt(tvQuantity.getText().toString());

                        // 값 갱신
                        item.setName(newName);
                        item.setPrice(newPrice);
                        item.setQuantity(newQuantity);
                        item.setChecked(false);

                        adapter.notifyItemChanged(position);
                        updateSummary();
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

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
            int price = data.getIntExtra("price", 0);
            int iconResId = data.getIntExtra("iconResId", R.drawable.ic_launcher_foreground);

            ShoppingItem newItem = new ShoppingItem(
                    name,
                    quantity,
                    price,
                    false,
                    iconResId
            );
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

    // ✅ 날짜 텍스트 갱신 메서드
    private void updateDateText() {
        String dateStr = dateFormat.format(currentCalendar.getTime());
        tvCurrentDate.setText(dateStr);
    }
}
