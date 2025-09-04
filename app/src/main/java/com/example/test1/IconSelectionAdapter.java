package com.example.test1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IconSelectionAdapter extends RecyclerView.Adapter<IconSelectionAdapter.IconViewHolder> {

    private List<IconItem> iconItems;
    private int selectedPosition = -1;
    private OnIconSelectedListener listener;

    // 인터페이스 정의
    public interface OnIconSelectedListener {
        void onIconSelected(IconItem iconItem, int position);
    }

    // 생성자
    public IconSelectionAdapter(List<IconItem> iconItems, OnIconSelectedListener listener) {
        this.iconItems = iconItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon_selection, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        IconItem item = iconItems.get(position);

        // 아이콘과 이름 설정
        holder.imageView.setImageResource(item.getResourceId());
        holder.textView.setText(item.getName());

        // 선택된 아이템 배경 설정
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_icon_bg);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.default_icon_bg);
        }

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            int oldPosition = selectedPosition;
            selectedPosition = currentPosition;

            // UI 업데이트
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);

            // 리스너 호출
            if (listener != null) {
                listener.onIconSelected(item, currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconItems != null ? iconItems.size() : 0;
    }

    // ViewHolder 클래스
    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_icon);
            textView = itemView.findViewById(R.id.tv_icon_name);
        }
    }
}