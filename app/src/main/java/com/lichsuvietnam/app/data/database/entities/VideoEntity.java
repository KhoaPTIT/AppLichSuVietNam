package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu thông tin video/tài liệu đa phương tiện.
 * Video có thể gắn với một sự kiện cụ thể hoặc hiển thị trong danh sách video chung.
 */
@Entity(tableName = "videos")
public class VideoEntity {
    // Khóa chính tự tăng của video.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ID sự kiện liên quan, dùng để lấy video trong EventDetailActivity.
    public long eventId;
    // ID YouTube thật hoặc chuỗi "search:<từ khóa>" để mở trang tìm kiếm YouTube.
    @NonNull public String youtubeId;
    // Tiêu đề video hiển thị trên UI.
    @NonNull public String title;
    // Mô tả ngắn của video.
    public String description;
    // Nguồn video, ví dụ VTV, YouTube, tài liệu lịch sử...
    public String source;
    // Thời lượng hiển thị.
    public String duration;
    public String thumbnail; // Tên drawable nội bộ hoặc URL thumbnail.

    /** Constructor tối thiểu khi tạo video trong dữ liệu seed. */
    public VideoEntity(@NonNull String youtubeId, @NonNull String title) {
        this.youtubeId = youtubeId;
        this.title = title;
    }

    /**
     * Trả về nguồn thumbnail phù hợp.
     * Nếu không có thumbnail riêng, video dạng search dùng ảnh mặc định,
     * còn video YouTube thật dùng URL ảnh thumbnail chuẩn của YouTube.
     */
    public String getThumbnailUrl() {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            return thumbnail;
        }
        if (youtubeId.startsWith("search:")) {
            return "video_default";
        }
        return "https://img.youtube.com/vi/" + youtubeId + "/mqdefault.jpg";
    }
}
