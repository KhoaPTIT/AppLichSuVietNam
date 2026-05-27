package com.lichsuvietnam.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.data.models.LearningPath;
import java.util.List;

/**
 * Adapter hiển thị danh sách lộ trình học trong chế độ học tập tương tác.
 * Mỗi item gồm icon, tên lộ trình, số bài đã hoàn thành và thanh tiến độ.
 */
public class LearningPathAdapter extends RecyclerView.Adapter<LearningPathAdapter.ViewHolder> {
    private final List<LearningPath> paths;
    private final OnPathClick listener;

    /** Callback khi người dùng chọn một lộ trình học. */
    public interface OnPathClick { void onClick(LearningPath path); }

    /** Khởi tạo adapter chỉ để hiển thị lộ trình, không xử lý click. */
    public LearningPathAdapter(List<LearningPath> paths) { this.paths = paths; this.listener = null; }

    /** Khởi tạo adapter có callback mở chi tiết lộ trình. */
    public LearningPathAdapter(List<LearningPath> paths, OnPathClick listener) { this.paths = paths; this.listener = listener; }

    /** Inflate layout item_learning_path.xml cho một lộ trình. */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_learning_path, parent, false));
    }

    /** Gắn dữ liệu lộ trình và tiến độ học vào item. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningPath path = paths.get(position);
        holder.tvIcon.setText(path.getIcon());
        holder.tvTitle.setText(path.getTitle());
        holder.tvProgress.setText(path.getCompletedLessons() + "/" + path.getTotalLessons() + " bài • " + path.getProgressPercent() + "%");
        holder.progressBar.setProgress(path.getProgressPercent());
        if (listener != null) holder.itemView.setOnClickListener(v -> listener.onClick(path));
    }

    /** Trả về số lộ trình học đang hiển thị. */
    @Override public int getItemCount() { return paths.size(); }

    /** ViewHolder lưu các view con của item lộ trình học. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvProgress;
        ProgressBar progressBar;
        ViewHolder(@NonNull View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tvIcon);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvProgress = v.findViewById(R.id.tvProgress);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
