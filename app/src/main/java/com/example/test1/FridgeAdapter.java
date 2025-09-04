package com.example.test1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.FridgeViewHolder> {

    private ArrayList<FridgeItem> items;
    private OnItemClickListener clickListener;
    private static final String TAG = "FridgeAdapter";

    public interface OnItemClickListener {
        void onItemClick(FridgeItem item, int position);
        void onItemLongClick(FridgeItem item, int position);
    }

    public FridgeAdapter(ArrayList<FridgeItem> items) {
        this.items = items;
    }

    public FridgeAdapter(ArrayList<FridgeItem> items, OnItemClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FridgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridge, parent, false);
        return new FridgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FridgeViewHolder holder, int position) {
        FridgeItem item = items.get(position);
        holder.bind(item);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemLongClick(item, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(ArrayList<FridgeItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void addItem(FridgeItem item) {
        if (items != null) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeItem(int position) {
        if (items != null && position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class FridgeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemImage;  // ✅ 변수명 변경
        private TextView tvItemName;    // ✅ 변수명 변경
        private TextView tvQuantity;
        private TextView tvStorage;
        private TextView tvExpiry;

        public FridgeViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ 올바른 ID로 수정
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvStorage = itemView.findViewById(R.id.tv_storage);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);

            // Null 체크
            if (ivItemImage == null) Log.e("FridgeViewHolder", "ivItemImage is null!");
            if (tvItemName == null) Log.e("FridgeViewHolder", "tvItemName is null!");
            if (tvQuantity == null) Log.e("FridgeViewHolder", "tvQuantity is null!");
            if (tvStorage == null) Log.e("FridgeViewHolder", "tvStorage is null!");
            if (tvExpiry == null) Log.e("FridgeViewHolder", "tvExpiry is null!");
        }

        public void bind(FridgeItem item) {
            if (item == null) return;

            if (ivItemImage != null) ivItemImage.setImageResource(item.getImageResId());
            if (tvItemName != null) tvItemName.setText(item.getName());
            if (tvQuantity != null) tvQuantity.setText(item.getQuantityText());
            if (tvStorage != null) tvStorage.setText(item.getStorage());
            if (tvExpiry != null) {
                tvExpiry.setText(item.getExpiry());
                updateExpiryColor(item.getExpiry());
            }
        }

        private void updateExpiryColor(String expiry) {
            if (tvExpiry == null || expiry == null) return;

            try {
                if (expiry.startsWith("D-")) {
                    int days = Integer.parseInt(expiry.substring(2));
                    if (days <= 3) {
                        tvExpiry.setTextColor(itemView.getContext().getResources()
                                .getColor(android.R.color.holo_red_dark));
                    } else if (days <= 7) {
                        tvExpiry.setTextColor(itemView.getContext().getResources()
                                .getColor(android.R.color.holo_orange_dark));
                    } else {
                        tvExpiry.setTextColor(itemView.getContext().getResources()
                                .getColor(android.R.color.black));
                    }
                } else if ("만료됨".equals(expiry)) {
                    tvExpiry.setTextColor(itemView.getContext().getResources()
                            .getColor(android.R.color.holo_red_dark));
                } else if ("오늘".equals(expiry)) {
                    tvExpiry.setTextColor(itemView.getContext().getResources()
                            .getColor(android.R.color.holo_orange_dark));
                } else {
                    tvExpiry.setTextColor(itemView.getContext().getResources()
                            .getColor(android.R.color.black));
                }
            } catch (NumberFormatException e) {
                tvExpiry.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.black));
            }
        }
    }
}