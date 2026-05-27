package com.lichsuvietnam.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.data.models.HistoryEvent;
import java.util.List;

/**
 * Adapter của RecyclerView cho danh sách Đường thời gian động.
 * Mỗi dòng hiển thị một sự kiện lịch sử gồm năm, tiêu đề, mô tả, ảnh minh họa
 * và phần chỉ báo timeline dọc.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private final List<HistoryEvent> events;
    private final OnItemClickListener listener;

    /**
     * Callback khi người dùng bấm vào một sự kiện để Fragment mở màn hình chi tiết.
     */
    public interface OnItemClickListener { void onItemClick(HistoryEvent event); }

    /**
     * Khởi tạo adapter với danh sách sự kiện đã được lọc.
     *
     * @param events danh sách sự kiện cần hiển thị theo đúng thứ tự
     * @param listener callback được gọi khi chọn một dòng
     */
    public TimelineAdapter(List<HistoryEvent> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    /**
     * Inflate layout item_timeline.xml và tạo ViewHolder cho RecyclerView.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Gắn dữ liệu một sự kiện lịch sử vào giao diện của một dòng.
     * ImageUtils bọc API ngoài Glide để tải ảnh minh họa cho sự kiện.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEvent event = events.get(position);
        holder.tvYear.setText("Năm " + event.getYear());
        holder.tvTitle.setText(event.getTitle());
        holder.tvDesc.setText(event.getDescription());
        holder.timelineLine.setVisibility(position == events.size() - 1 ? View.INVISIBLE : View.VISIBLE);

        ImageUtils.load(holder.itemView.getContext(), event.getImageUrl(), holder.ivImage);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(event));
    }

    /**
     * Trả về số dòng mà RecyclerView cần hiển thị.
     */
    @Override
    public int getItemCount() { return events.size(); }

    /**
     * Lưu tham chiếu các view con của một dòng timeline để tránh gọi
     * findViewById lặp lại khi RecyclerView cuộn.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvYear, tvTitle, tvDesc;
        ImageView ivImage;
        View timelineDot, timelineLine;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivImage = itemView.findViewById(R.id.ivImage);
            timelineDot = itemView.findViewById(R.id.timelineDot);
            timelineLine = itemView.findViewById(R.id.timelineLine);
        }
    }
}
