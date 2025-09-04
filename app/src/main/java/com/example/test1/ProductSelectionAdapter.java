package com.example.test1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductSelectionAdapter extends RecyclerView.Adapter<ProductSelectionAdapter.ProductViewHolder> {

    private List<Product> products;   // ✅ ProductItem → Product
    private OnProductSelectedListener listener;

    public interface OnProductSelectedListener {
        void onProductSelected(Product product, boolean isSelected);  // ✅ 변경
    }

    public ProductSelectionAdapter(List<Product> products, OnProductSelectedListener listener) {
        this.products = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_selection, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);   // ✅ Product 사용
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;
        private View itemContainer;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(Product product) {
            ivProduct.setImageResource(product.getIconResId());  // ✅ 메서드명 수정
            tvProductName.setText(product.getName());            // 실제 선택한 이름 표시

            // 선택 상태에 따른 UI 변경
            updateSelectionState(product.isSelected());

            // 클릭 리스너 설정
            itemContainer.setOnClickListener(v -> {
                product.setSelected(!product.isSelected());
                updateSelectionState(product.isSelected());

                if (listener != null) {
                    listener.onProductSelected(product, product.isSelected());
                }
            });
        }

        private void updateSelectionState(boolean isSelected) {
            if (isSelected) {
                itemContainer.setBackgroundColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_blue_light));
                itemContainer.setAlpha(0.7f);
            } else {
                itemContainer.setBackgroundColor(itemView.getContext().getResources()
                        .getColor(android.R.color.transparent));
                itemContainer.setAlpha(1.0f);
            }
        }
    }
}
