package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity Room ghi nhận một lượt thích bình luận của người dùng.
 * Index unique trên userId và commentId giúp tránh thích trùng cùng một bình luận.
 */
@Entity(tableName = "comment_likes",
        indices = {@Index(value = {"userId", "commentId"}, unique = true)})
public class CommentLikeEntity {
    // Khóa chính tự tăng của lượt thích bình luận.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng đã thích bình luận.
    public long userId;
    // Bình luận được thích.
    public long commentId;
    // Thời điểm tạo lượt thích.
    public long createdAt;

    /** Khởi tạo lượt thích bình luận và tự gán thời điểm tạo. */
    public CommentLikeEntity(long userId, long commentId) {
        this.userId = userId;
        this.commentId = commentId;
        this.createdAt = System.currentTimeMillis();
    }
}
