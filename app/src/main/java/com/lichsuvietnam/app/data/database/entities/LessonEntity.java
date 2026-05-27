package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu một bài học thuộc lộ trình học.
 * Bài học được hiển thị trong LearningPathDetailActivity và có thể mở QuizActivity.
 */
@Entity(tableName = "lessons")
public class LessonEntity {
    // Khóa chính tự tăng của bài học.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ID lộ trình chứa bài học.
    public long pathId;
    // Tiêu đề bài học.
    @NonNull public String title;
    // Mô tả ngắn của bài học.
    public String description;
    // Thứ tự bài học trong lộ trình.
    public int orderIndex;

    /** Khởi tạo bài học thuộc một lộ trình cụ thể. */
    public LessonEntity(long pathId, @NonNull String title, String description, int orderIndex) {
        this.pathId = pathId;
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
    }
}
