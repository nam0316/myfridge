package com.example.test1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {

    private ArrayList<FridgeItem> items;

    public FridgeAdapter(ArrayList<FridgeItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FridgeItem item = items.get(position);

        if (item != null) {
            holder.imageView.setImageResource(item.getImageResId());
            holder.nameTextView.setText(item.getName());
            holder.storageTextView.setText(item.getStorage());
            holder.quantityTextView.setText(item.getQuantity());
            holder.expiryTextView.setText(item.getExpiry());

            // 유통기한에 따른 색상 변경
            if (item.getExpiry().startsWith("D-")) {
                try {
                    int days = Integer.parseInt(item.getExpiry().substring(2));
                    if (days <= 3) {
                        // 임박한 경우 빨간색
                        holder.expiryTextView.setTextColor(
                                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark)
                        );
                    } else if (days <= 7) {
                        // 주의 필요한 경우 주황색
                        holder.expiryTextView.setTextColor(
                                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark)
                        );
                    } else {
                        // 여유 있는 경우 기본 색상
                        holder.expiryTextView.setTextColor(
                                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black)
                        );
                    }
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 경우 기본 색상
                    holder.expiryTextView.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black)
                    );
                }
            } else {
                // "신선함" 등의 경우 녹색
                holder.expiryTextView.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark)
                );
            }
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView storageTextView;
        TextView quantityTextView;
        TextView expiryTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_image);
            nameTextView = itemView.findViewById(R.id.tv_item_name);
            storageTextView = itemView.findViewById(R.id.tv_item_storage);
            quantityTextView = itemView.findViewById(R.id.tv_item_quantity);
            expiryTextView = itemView.findViewById(R.id.tv_item_expiry);
        }
    }
}