package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu một lộ trình học.
 * Mỗi lộ trình gồm nhiều bài học và được hiển thị ở LearnFragment.
 */
@Entity(tableName = "learning_paths")
public class LearningPathEntity {
    // Khóa chính tự tăng của lộ trình.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Tên lộ trình học.
    @NonNull public String title;
    // Icon/emoji đại diện cho lộ trình.
    public String icon;
    // Tổng số bài học trong lộ trình.
    public int totalLessons;
    // Thứ tự hiển thị của lộ trình trên UI.
    public int orderIndex;

    /** Khởi tạo lộ trình học để seed hoặc thêm vào database. */
    public LearningPathEntity(@NonNull String title, String icon, int totalLessons, int orderIndex) {
        this.title = title;
        this.icon = icon;
        this.totalLessons = totalLessons;
        this.orderIndex = orderIndex;
    }
}
