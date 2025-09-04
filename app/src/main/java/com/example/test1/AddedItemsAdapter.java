package com.example.test1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddedItemsAdapter extends RecyclerView.Adapter<AddedItemsAdapter.AddedItemViewHolder> {

    private List<FridgeItem> items;
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public AddedItemsAdapter(List<FridgeItem> items, OnItemDeleteListener deleteListener) {
        this.items = items;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AddedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_added_product, parent, false);
        return new AddedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedItemViewHolder holder, int position) {
        FridgeItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() { return items.size(); }

    public class AddedItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemImage;
        private TextView tvItemName;
        private TextView tvStorage;
        private TextView tvQuantity;
        private TextView tvExpiry;
        private ImageView btnDelete;

        public AddedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvStorage = itemView.findViewById(R.id.tv_storage);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(FridgeItem item, int position) {
            ivItemImage.setImageResource(item.getImageResId());
            tvItemName.setText(item.getName());
            tvStorage.setText(item.getStorage());
            tvQuantity.setText(item.getQuantityText()); // 여기 수정
            tvExpiry.setText(item.getExpiry());

            setExpiryColor(item.getExpiry());

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onItemDelete(position);
            });
        }

        private void setExpiryColor(String expiry) {
            if (expiry == null) return;

            if (expiry.startsWith("D-")) {
                try {
                    int days = Integer.parseInt(expiry.substring(2));
                    if (days <= 3) tvExpiry.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                    else if (days <= 7) tvExpiry.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark));
                    else tvExpiry.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                } catch (NumberFormatException e) {
                    tvExpiry.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                }
            } else {
                tvExpiry.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
            }
        }
    }
}
