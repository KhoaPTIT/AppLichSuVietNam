package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu một từ khóa tìm kiếm của người dùng.
 * Dữ liệu này phục vụ phần gợi ý "Tìm kiếm gần đây" trong SearchActivity.
 */
@Entity(tableName = "search_history")
public class SearchHistoryEntity {
    // Khóa chính tự tăng của lịch sử tìm kiếm.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng đã thực hiện tìm kiếm.
    public long userId;
    // Từ khóa tìm kiếm.
    @NonNull public String query;
    // Thời điểm tìm kiếm, dùng để sắp xếp truy vấn mới nhất lên trước.
    public long createdAt;

    /** Khởi tạo lịch sử tìm kiếm và tự gán thời điểm tạo. */
    public SearchHistoryEntity(long userId, @NonNull String query) {
        this.userId = userId;
        this.query = query;
        this.createdAt = System.currentTimeMillis();
    }
}
