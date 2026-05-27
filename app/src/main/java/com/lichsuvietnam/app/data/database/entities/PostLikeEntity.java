package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.Index;

/**
 * Entity Room ghi nhận một lượt thích bài viết của người dùng.
 * Index unique trên userId và postId giúp ngăn một người thích cùng bài nhiều lần.
 */
@Entity(tableName = "post_likes",
        indices = {@Index(value = {"userId", "postId"}, unique = true)})
public class PostLikeEntity {
    // Khóa chính tự tăng của lượt thích.
    @androidx.room.PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng đã thích bài viết.
    public long userId;
    // Bài viết được thích.
    public long postId;
    // Thời điểm tạo lượt thích.
    public long createdAt;

    /** Khởi tạo lượt thích bài viết và tự gán thời điểm tạo. */
    public PostLikeEntity(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = System.currentTimeMillis();
    }
}
