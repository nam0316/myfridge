package com.example.myapplication.ui.shopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

    private List<ShoppingItem> items;
    private OnItemCheckedListener listener;

    // ✅ 체크 이벤트 콜백 인터페이스
    public interface OnItemCheckedListener {
        void onItemChecked();
    }

    public ShoppingAdapter(List<ShoppingItem> items, OnItemCheckedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbItem;
        TextView tvName, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cb_item);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
        }
    }

    @NonNull
    @Override
    public ShoppingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapter.ViewHolder holder, int position) {
        ShoppingItem item = items.get(position);

        // 기존 체크 리스너 제거 (재활용 방지)
        holder.cbItem.setOnCheckedChangeListener(null);

        // 값 세팅
        holder.cbItem.setChecked(item.isChecked());
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice() + "원");

        // 체크 이벤트
        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (listener != null) listener.onItemChecked();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ✅ 체크된 항목 삭제
    public void removeCheckedItems() {
        items.removeIf(ShoppingItem::isChecked);
        notifyDataSetChanged();
    }

    // ✅ 체크 안된 항목 총 금액
    public int getUncheckedTotal() {
        int total = 0;
        for (ShoppingItem item : items) {
            if (!item.isChecked()) {
                total += item.getPrice();
            }
        }
        return total;
    }
}
