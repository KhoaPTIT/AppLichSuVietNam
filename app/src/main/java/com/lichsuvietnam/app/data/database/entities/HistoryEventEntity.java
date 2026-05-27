package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu thông tin sự kiện lịch sử.
 * Dữ liệu này được dùng bởi tìm kiếm/lọc sự kiện, chi tiết sự kiện,
 * thư viện hình ảnh, đường thời gian động và bookmark.
 */
@Entity(tableName = "history_events")
public class HistoryEventEntity {
    // Khóa chính tự tăng của sự kiện.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Năm hiển thị dạng chữ, ví dụ "938" hoặc "40 SCN".
    @NonNull public String year;
    // Tiêu đề sự kiện.
    @NonNull public String title;
    // Mô tả ngắn dùng trong danh sách.
    @NonNull public String description;
    // Nội dung đầy đủ dùng ở màn hình chi tiết.
    public String fullContent;
    // URL hoặc tên drawable ảnh minh họa.
    public String imageUrl;
    // Địa điểm xảy ra sự kiện.
    public String location;
    public String period;       // Giai đoạn: "Dựng nước", "Bắc thuộc", "Phong kiến", "Cận đại", "Hiện đại".
    public String category;     // Danh mục: chiến tranh, chính trị, văn hóa, kinh tế...
    public String keyFigures;   // Danh sách tên nhân vật liên quan, lưu dạng chuỗi phân tách.
    public int yearNumeric;     // Năm dạng số để sắp xếp và lọc bằng timeline.

    /** Constructor tối thiểu khi tạo sự kiện, các trường phụ có thể gán sau. */
    public HistoryEventEntity(@NonNull String year, @NonNull String title, @NonNull String description) {
        this.year = year;
        this.title = title;
        this.description = description;
    }
}
