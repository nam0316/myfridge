package com.example.myapplication.ui.shopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

    private List<ShoppingItem> items;
    private OnItemCheckedListener checkedListener;
    private OnItemClickListener clickListener;

    // 체크 이벤트 인터페이스
    public interface OnItemCheckedListener {
        void onItemChecked();
    }

    // 아이템 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public ShoppingAdapter(List<ShoppingItem> items, OnItemCheckedListener listener) {
        this.items = items;
        this.checkedListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbItem;
        ImageView ivIcon;
        TextView tvName, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cb_item);
            ivIcon = itemView.findViewById(R.id.iv_item_icon);
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

        holder.cbItem.setOnCheckedChangeListener(null);

        holder.cbItem.setChecked(item.isChecked());
        holder.tvName.setText(item.getName() + " (" + item.getQuantity() + "개)");
        holder.tvPrice.setText(item.getPrice() + "원");

        if (item.getIconResId() != 0) {
            holder.ivIcon.setImageResource(item.getIconResId());
        }

        // ✅ 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });

        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (checkedListener != null) checkedListener.onItemChecked();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeCheckedItems() {
        items.removeIf(ShoppingItem::isChecked);
        notifyDataSetChanged();
    }

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
