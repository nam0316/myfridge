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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;
    private int selectedPosition = -1;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        boolean isSelected = position == selectedPosition;
        holder.bind(product, isSelected);

        final int currentPosition = position; // ✅ 복사
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = currentPosition;

            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    // ✅ 여기에 추가
    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductIcon;
        private TextView tvProductName;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductIcon = itemView.findViewById(R.id.iv_product_icon);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
        }

        public void bind(Product product, boolean isSelected) {
            ivProductIcon.setImageResource(product.getIconResId());
            tvProductName.setText(product.getName());

            // 선택 상태에 따른 스타일 변경
            itemView.setSelected(isSelected);
            if (isSelected) {
                tvProductName.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
            } else {
                tvProductName.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
            }
        }
    }
}