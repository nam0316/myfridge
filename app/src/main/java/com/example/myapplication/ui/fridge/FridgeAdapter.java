package com.example.myapplication.ui.fridge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {

    private List<FridgeItem> items;
    private List<FridgeItem> originalList;

    public FridgeAdapter(List<FridgeItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.originalList = new ArrayList<>(this.items);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvCategory, tvQuantity, tvExpiry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_item_icon);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_item_expiry);
        }
    }

    @NonNull
    @Override
    public FridgeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FridgeAdapter.ViewHolder holder, int position) {
        FridgeItem item = items.get(position);

        if (item.getIconResId() != 0) {
            holder.ivIcon.setImageResource(item.getIconResId());
            holder.ivIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivIcon.setVisibility(View.GONE);
        }

        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvQuantity.setText(item.getQuantity() + "개");
        holder.tvExpiry.setText(item.getDisplayExpiryDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<FridgeItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        this.originalList = new ArrayList<>(this.items);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        items.clear();
        if (query == null || query.trim().isEmpty()) {
            items.addAll(originalList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (FridgeItem item : originalList) {
                if (item.getName().toLowerCase().contains(lowerQuery) ||
                        item.getCategory().toLowerCase().contains(lowerQuery)) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
