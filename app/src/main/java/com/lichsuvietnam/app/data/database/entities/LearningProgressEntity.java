package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity Room lưu tiến độ học ở cấp lộ trình.
 * Index unique userId + pathId bảo đảm mỗi người dùng chỉ có một bản ghi tiến độ
 * cho mỗi lộ trình.
 */
@Entity(tableName = "learning_progress",
        indices = {@Index(value = {"userId", "pathId"}, unique = true)})
public class LearningProgressEntity {
    // Khóa chính tự tăng của bản ghi tiến độ.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng sở hữu tiến độ.
    public long userId;
    // Lộ trình đang theo dõi.
    public long pathId;
    // Số bài học đã hoàn thành trong lộ trình.
    public int completedLessons;
    // Thời điểm truy cập/cập nhật gần nhất.
    public long lastAccessedAt;

    /** Khởi tạo tiến độ lộ trình với số bài hoàn thành ban đầu bằng 0. */
    public LearningProgressEntity(long userId, long pathId) {
        this.userId = userId;
        this.pathId = pathId;
        this.completedLessons = 0;
        this.lastAccessedAt = System.currentTimeMillis();
    }
}
