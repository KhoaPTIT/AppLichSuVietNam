package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu bài viết trong chức năng cộng đồng.
 * Dữ liệu này được hiển thị ở CommunityFragment và ThreadActivity.
 */
@Entity(tableName = "posts")
public class PostEntity {
    // Khóa chính tự tăng của bài viết.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ID người đăng bài.
    public long userId;
    // Tên tác giả hiển thị trên UI.
    @NonNull public String authorName;
    // Nội dung bài viết.
    @NonNull public String content;
    // Chủ đề bài viết: thảo luận, hỏi đáp, đánh giá...
    public String topic;
    // Tổng số lượt thích hiện tại.
    public int likes;
    // Tổng số bình luận, được đồng bộ từ bảng comments.
    public int commentsCount;
    // Thời điểm tạo bài, dùng để sắp xếp và hiển thị thời gian tương đối.
    public long createdAt;

    /** Khởi tạo bài viết mới với số lượt thích/bình luận mặc định bằng 0. */
    public PostEntity(long userId, @NonNull String authorName, @NonNull String content, String topic) {
        this.userId = userId;
        this.authorName = authorName;
        this.content = content;
        this.topic = topic;
        this.likes = 0;
        this.commentsCount = 0;
        this.createdAt = System.currentTimeMillis();
    }
}
