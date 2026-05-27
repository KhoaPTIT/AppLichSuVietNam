package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu bình luận trong một bài viết cộng đồng.
 * Hỗ trợ bình luận cha và reply thông qua parentCommentId.
 */
@Entity(tableName = "comments")
public class CommentEntity {
    // Khóa chính tự tăng của bình luận.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ID bài viết chứa bình luận.
    public long postId;
    // ID người bình luận.
    public long userId;
    // Tên người bình luận.
    @NonNull public String authorName;
    // Nội dung bình luận.
    @NonNull public String text;
    // Số lượt thích bình luận.
    public int likes;
    // true nếu đây là bình luận trả lời.
    public boolean isReply;
    // ID bình luận cha; bằng 0 nếu là bình luận cấp cao nhất.
    public long parentCommentId;
    // Thời điểm tạo bình luận.
    public long createdAt;

    /** Khởi tạo bình luận mới với trạng thái mặc định là bình luận cha. */
    public CommentEntity(long postId, long userId, @NonNull String authorName, @NonNull String text) {
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.text = text;
        this.likes = 0;
        this.isReply = false;
        this.parentCommentId = 0;
        this.createdAt = System.currentTimeMillis();
    }
}
