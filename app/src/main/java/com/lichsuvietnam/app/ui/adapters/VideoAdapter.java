package com.lichsuvietnam.app.ui.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.utils.ImageUtils;
import com.lichsuvietnam.app.data.database.entities.VideoEntity;
import java.util.List;

/**
 * Adapter dùng chung để hiển thị video liên quan hoặc danh sách video.
 * Mỗi item gồm thumbnail, tiêu đề, nguồn/thông tin mô tả và thời lượng.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VH> {
    private List<VideoEntity> videos;
    private final OnVideoClick listener;

    /** Callback khi người dùng chọn một video. */
    public interface OnVideoClick { void onClick(VideoEntity video); }

    /** Khởi tạo adapter với danh sách video và callback mở video. */
    public VideoAdapter(List<VideoEntity> videos, OnVideoClick listener) {
        this.videos = videos;
        this.listener = listener;
    }

    /** Cập nhật danh sách video mới và yêu cầu RecyclerView vẽ lại. */
    public void updateData(List<VideoEntity> newData) {
        this.videos = newData;
        notifyDataSetChanged();
    }

    /** Inflate layout item_video.xml cho một item video. */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
    }

    /**
     * Gắn dữ liệu video vào item.
     * ImageUtils gọi Glide để tải thumbnail và bo góc ảnh.
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        VideoEntity v = videos.get(pos);
        h.txtTitle.setText(v.title);
        h.txtSource.setText((v.description != null ? v.description : "") + " · " + (v.source != null ? v.source : ""));
        h.txtDuration.setText(v.duration != null ? v.duration : "");

        ImageUtils.loadRounded(h.itemView.getContext(), v.getThumbnailUrl(), h.imgThumb, 4);

        h.itemView.setOnClickListener(view -> listener.onClick(v));
    }

    /** Trả về số video hiện có trong adapter. */
    @Override public int getItemCount() { return videos != null ? videos.size() : 0; }

    /** ViewHolder lưu các view con của item video. */
    static class VH extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtTitle, txtSource, txtDuration;
        VH(View v) {
            super(v);
            imgThumb = v.findViewById(R.id.imgVideoThumb);
            txtTitle = v.findViewById(R.id.txtVideoTitle);
            txtSource = v.findViewById(R.id.txtVideoSource);
            txtDuration = v.findViewById(R.id.txtVideoDuration);
        }
    }
}
