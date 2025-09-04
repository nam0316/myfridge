package com.example.test1;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShoppingItemsAdapter extends RecyclerView.Adapter<ShoppingItemsAdapter.ShoppingItemViewHolder> {

    private List<ShoppingFragment.ShoppingItem> items;
    private OnItemCheckedListener checkedListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemCheckedListener {
        void onItemChecked(ShoppingFragment.ShoppingItem item, boolean isChecked);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(ShoppingFragment.ShoppingItem item);
    }

    public ShoppingItemsAdapter(List<ShoppingFragment.ShoppingItem> items,
                                OnItemCheckedListener checkedListener,
                                OnItemLongClickListener longClickListener) {
        this.items = items;
        this.checkedListener = checkedListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position) {
        ShoppingFragment.ShoppingItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<ShoppingFragment.ShoppingItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxItem;
        private TextView tvItemName, tvItemInfo, tvItemStatus;

        public ShoppingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxItem = itemView.findViewById(R.id.checkbox_item);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemInfo = itemView.findViewById(R.id.tv_item_info);
            tvItemStatus = itemView.findViewById(R.id.tv_item_status);
        }

        public void bind(ShoppingFragment.ShoppingItem item) {
            // 체크박스 상태 설정
            checkboxItem.setOnCheckedChangeListener(null); // 기존 리스너 제거
            checkboxItem.setChecked(item.isCompleted());

            // 아이템 정보 설정
            tvItemName.setText(item.getName());
            tvItemInfo.setText(item.getCategory() + " • " + String.format("%,d원", item.getPrice()));

            // 완료 상태에 따른 UI 변경
            if (item.isCompleted()) {
                tvItemName.setPaintFlags(tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvItemName.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                tvItemInfo.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                tvItemStatus.setText("완료!");
                tvItemStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                itemView.setAlpha(0.6f);
            } else {
                tvItemName.setPaintFlags(tvItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvItemName.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                tvItemInfo.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                tvItemStatus.setText("수량: " + item.getQuantity() + "개");
                tvItemStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark));
                itemView.setAlpha(1.0f);
            }

            // 체크박스 리스너 설정
            checkboxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkedListener != null) {
                    checkedListener.onItemChecked(item, isChecked);
                    notifyItemChanged(getAdapterPosition()); // UI 즉시 업데이트
                }
            });

            // 길게 누르기 리스너
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(item);
                    return true;
                }
                return false;
            });
        }
    }
}