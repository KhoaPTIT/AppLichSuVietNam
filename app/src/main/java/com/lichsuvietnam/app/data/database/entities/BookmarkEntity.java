package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity Room lưu một mục yêu thích của người dùng.
 * Mỗi bản ghi liên kết userId với eventId để biết người dùng đã lưu sự kiện nào.
 */
@Entity(tableName = "bookmarks")
public class BookmarkEntity {
    // Khóa chính tự tăng của bookmark.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng sở hữu bookmark.
    public long userId;
    // Sự kiện được lưu.
    public long eventId;
    // Tên bộ sưu tập, ví dụ "Yêu thích".
    public String collectionName;
    // Thời điểm lưu, dùng để sắp xếp mục mới nhất lên trước.
    public long createdAt;

    /** Khởi tạo bookmark mới và tự gán thời điểm tạo. */
    public BookmarkEntity(long userId, long eventId, String collectionName) {
        this.userId = userId;
        this.eventId = eventId;
        this.collectionName = collectionName;
        this.createdAt = System.currentTimeMillis();
    }
}
