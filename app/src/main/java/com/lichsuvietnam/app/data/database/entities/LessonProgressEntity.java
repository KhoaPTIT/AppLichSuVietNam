package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity Room lưu tiến độ của từng bài học.
 * Index unique userId + lessonId giúp mỗi người dùng chỉ có một trạng thái
 * cho mỗi bài học.
 */
@Entity(tableName = "lesson_progress",
        indices = {@Index(value = {"userId", "lessonId"}, unique = true)})
public class LessonProgressEntity {
    // Khóa chính tự tăng của tiến độ bài học.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng sở hữu tiến độ.
    public long userId;
    // Bài học được theo dõi.
    public long lessonId;
    // true nếu bài học đã hoàn thành.
    public boolean completed;
    // Điểm đạt được trong quiz của bài học.
    public int score;
    // Thời điểm hoàn thành bài học.
    public long completedAt;

    /** Khởi tạo tiến độ bài học với trạng thái chưa hoàn thành. */
    public LessonProgressEntity(long userId, long lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.completed = false;
        this.score = 0;
        this.completedAt = 0;
    }
}
