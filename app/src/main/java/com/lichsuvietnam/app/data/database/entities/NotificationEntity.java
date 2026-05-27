package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "notifications")
public class NotificationEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId; // who receives the notification
    public long fromUserId; // who triggered it
    @NonNull public String fromUserName;
    @NonNull public String type; // "comment", "like", etc.
    @NonNull public String message;
    public long postId; // related post
    public long commentId; // related comment
    public boolean isRead;
    public long createdAt;

    public NotificationEntity(long userId, long fromUserId, @NonNull String fromUserName,
                              @NonNull String type, @NonNull String message, long postId) {
        this.userId = userId;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.type = type;
        this.message = message;
        this.postId = postId;
        this.commentId = 0;
        this.isRead = false;
        this.createdAt = System.currentTimeMillis();
    }
}
